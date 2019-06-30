package br.unb.cic.soot.boomerang

import boomerang.{BackwardQuery, Boomerang, ForwardQuery}
import boomerang.callgraph.ObservableICFG
import boomerang.jimple.{Statement, Val}
import boomerang.results.BackwardBoomerangResults
import boomerang.seedfactory.SeedFactory
import soot.{PointsToAnalysis, PointsToSet}
import soot.jimple.{AssignStmt, InstanceFieldRef, Stmt}
import wpds.impl.Weight

import scala.collection.mutable.Set

class Solver(val cfg: ObservableICFG[soot.Unit, soot.SootMethod]) extends Boomerang(new BoomerangOptions) {
  override def icfg: ObservableICFG[soot.Unit, soot.SootMethod] = cfg
  override def getSeedFactory: SeedFactory[Weight.NoWeight] = null

  def findAllocationSites(method: soot.SootMethod, unit: soot.Unit) : Set[ForwardQuery] = {
    if(unit.isInstanceOf[soot.jimple.AssignStmt]) {
      val stmt = unit.asInstanceOf[soot.jimple.AssignStmt]
      val ret : Set[ForwardQuery] = Set.empty
      if(stmt.getLeftOp.isInstanceOf[soot.Local]) {
        val local = stmt.getLeftOp.asInstanceOf[soot.Local]
        cfg.getSuccsOf(unit).forEach(s => {
          val query = new BackwardQuery(new boomerang.jimple.Statement(s.asInstanceOf[Stmt], method), new Val(local, method))
          val res : BackwardBoomerangResults[Weight.NoWeight] = solve(query)
          println(res.getAllocationSites.size())
          res.getAllocationSites.keySet().forEach(f => ret += f)
        })
        return ret
      }
    }
    return Set.empty
  }

  def findDefinitions(fieldRef: InstanceFieldRef, query: ForwardQuery, pointsToAnalysis: PointsToAnalysis) : Set[Statement] = {
    val returnSet : scala.collection.mutable.Set[Statement] = scala.collection.mutable.Set.empty
    val set1 = pointsToAnalysis.reachingObjects(fieldRef.getBase.asInstanceOf[soot.Local])
    val results = solve(query)
    val table = results.asStatementValWeightTable()
    table.columnKeySet().forEach(v => {
      table.columnMap().get(v).keySet()
        .stream()
        .filter(stmt => stmt.getUnit.isPresent)
        .forEach(stmt => {
            if(stmt.getUnit.get().isInstanceOf[AssignStmt] && stmt.getUnit.get().asInstanceOf[AssignStmt].getLeftOp.isInstanceOf[InstanceFieldRef]) {
              val instanceFieldRef = stmt.getUnit.get().asInstanceOf[AssignStmt].getLeftOp.asInstanceOf[InstanceFieldRef]
              val local : soot.Local = instanceFieldRef.getBase.asInstanceOf[soot.Local]
              val set2 = pointsToAnalysis.reachingObjects(local)
              if(set1.hasNonEmptyIntersection(set2) && fieldRef.getField.equals(instanceFieldRef.getField)) {
                returnSet += stmt
              }
            }
      })
    })
    return returnSet
  }
}
