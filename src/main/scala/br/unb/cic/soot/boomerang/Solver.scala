package br.unb.cic.soot.boomerang

import boomerang.{BackwardQuery, Boomerang, ForwardQuery}
import boomerang.callgraph.ObservableICFG
import boomerang.jimple.Val
import boomerang.results.{AbstractBoomerangResults, BackwardBoomerangResults}
import boomerang.seedfactory.SeedFactory
import soot.SootMethod
import soot.jimple.Stmt
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
}
