package br.unb.cic.soot.svfa.jimple

import java.util

import boomerang.callgraph.ObservableDynamicICFG
import boomerang.preanalysis.BoomerangPretransformer
import br.unb.cic.soot.boomerang.Solver
import br.unb.cic.soot.graph.{Node, SinkNode}
import br.unb.cic.soot.svfa.{SVFA, SourceSinkDef}
import scalax.collection.GraphPredef._
import soot.jimple._
import soot.jimple.spark.pag
import soot.jimple.spark.pag.{AllocNode, PAG}
import soot.jimple.spark.sets.{DoublePointsToSet, HybridPointsToSet, P2SetVisitor, PointsToSetInternal}
import soot.toolkits.graph.ExceptionalUnitGraph
import soot.toolkits.scalar.SimpleLocalDefs
import soot.{Local, PointsToSet, Scene, SceneTransformer, SootMethod, Transform}



/**
  * A Jimple based implementation of
  * SVFA.
  */
abstract class JSVFA extends SVFA with SourceSinkDef {

  var solver : Solver = _
  var observableDynamicICFG : ObservableDynamicICFG = _
  var methods = 0
  var traversedMethods : scala.collection.mutable.Set[SootMethod] = scala.collection.mutable.Set.empty

  def createSceneTransform(): (String, Transform) = ("wjtp", new Transform("wjtp.svfa", new Transformer()))

  def configurePackages(): List[String] = List("cg", "wjtp")

  def beforeGraphConstruction() { }
  def afterGraphConstruction() { }

  class Transformer extends SceneTransformer {
    override def internalTransform(phaseName: String, options: util.Map[String, String]): Unit = {
      BoomerangPretransformer.v().reset()
      BoomerangPretransformer.v().apply()

      pointsToAnalysis = Scene.v().getPointsToAnalysis
      observableDynamicICFG = new ObservableDynamicICFG(false)
      solver = new Solver(observableDynamicICFG)

      Scene.v().getEntryPoints.forEach(method => {
        traverse(method)
        methods = methods + 1
      })
    }
  }

  def traverse(method: SootMethod) : Unit = {
    if(traversedMethods.contains(method)) {
      return
    }

    traversedMethods.add(method)

    val body  = method.retrieveActiveBody()
    println(body)
    val graph = new ExceptionalUnitGraph(body)
    val defs  = new SimpleLocalDefs(graph)

    body.getUnits.forEach(unit => {
      val v = Statement.convert(unit)

      v match {
        case AssignStmt(base) => traverse(AssignStmt(base), method, defs)
        case InvokeStmt(base) => traverse(InvokeStmt(base), method, defs)
        case _ => println(unit)
      }
    })
  }

  def traverse(assignStmt: AssignStmt, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val targetStmt = assignStmt.stmt
    if(targetStmt.getRightOp.isInstanceOf[Local]) {
      copyRule(assignStmt, method, defs)
    }
    else if(targetStmt.getRightOp.isInstanceOf[InvokeExpr]) {
      val exp = targetStmt.getRightOp.asInstanceOf[InvokeExpr]
      invokeRule(assignStmt, exp, method, defs)
    }
    else if(targetStmt.getRightOp.isInstanceOf[InstanceFieldRef]) {
      val exp = targetStmt.getRightOp.asInstanceOf[InstanceFieldRef]
      loadRule(assignStmt, exp, method, defs)
    }
  }

  def traverse(stmt: InvokeStmt, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val exp = stmt.stmt.getInvokeExpr
    invokeRule(stmt, exp, method, defs)
  }

  private def copyRule(assignStmt: AssignStmt, method: SootMethod, defs: SimpleLocalDefs) = {
    val targetStmt = assignStmt.stmt
    val local = targetStmt.getRightOp.asInstanceOf[Local]
    defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
      val source = createNode(method, sourceStmt)
      val target = createNode(method, targetStmt)
      svg += source ~> target
    })
  }

  private def invokeRule(callStmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs): Unit = {
    val callee = exp.getMethod

    if(analyze(callStmt.base) == SinkNode()) {
      defsToCallOfSinkMethod(callStmt, exp, caller, defs)
    }

    var pmtCount = 0
    val body = callee.retrieveActiveBody()
    val g = new ExceptionalUnitGraph(body)
    val calleeDefs = new SimpleLocalDefs(g)

    body.getUnits.forEach(s => {
      if(isParameterInitStmt(exp, pmtCount, s)) {
        defsToFormalArgs(callStmt, caller, defs, s, exp, callee, pmtCount)
        pmtCount = pmtCount + 1
      }
      if(isAssignReturnStmt(callStmt.base, s)) {
        defsToCallSite(caller, callee, calleeDefs, callStmt.base, s)
      }
    })
    traverse(callee)
  }

  private def loadRule(stmt: AssignStmt, ref: InstanceFieldRef, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val base = ref.getBase

    if(base.isInstanceOf[Local] && pointsToAnalysis.isInstanceOf[PAG]) {
      val pta = pointsToAnalysis.asInstanceOf[PAG]
      val allocations = pta.reachingObjects(base.asInstanceOf[Local], ref.getField).asInstanceOf[DoublePointsToSet].getNewSet
      allocations.asInstanceOf[HybridPointsToSet].forall(new AllocationVisitor(method, stmt.base))
    }
  }




  private def defsToCallSite(caller: SootMethod, callee: SootMethod, calleeDefs: SimpleLocalDefs, callStmt: soot.Unit, retStmt: soot.Unit) = {
    val local = retStmt.asInstanceOf[ReturnStmt].getOp.asInstanceOf[Local]
    calleeDefs.getDefsOfAt(local, retStmt).forEach(sourceStmt => {
      val source = createNode(callee, sourceStmt)
      val target = createNode(caller, callStmt)
      svg += source ~> target
    })
  }

  private def defsToFormalArgs(stmt: Statement, caller: SootMethod, defs: SimpleLocalDefs, assignStmt: soot.Unit, exp: InvokeExpr, callee: SootMethod, pmtCount: Int) = {
    val local = exp.getArg(pmtCount).asInstanceOf[Local]
    defs.getDefsOfAt(local, stmt.base).forEach(sourceStmt => {
      val source = createNode(caller, sourceStmt)
      val target = createNode(callee, assignStmt)
      svg += source ~> target
    })
  }

  private def defsToCallOfSinkMethod(stmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs) = {
    exp.getArgs.stream().filter(a => a.isInstanceOf[Local]).forEach(a => {
      val local = a.asInstanceOf[Local]
      val targetStmt = stmt.base
      defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
        val source = createNode(caller, sourceStmt)
        val target = createNode(caller, targetStmt)
        svg + source ~> target
      })
    })
  }

  def createNode(method: SootMethod, stmt: soot.Unit): Node =
    Node(method.getDeclaringClass.toString, method.getSignature, stmt.toString(), stmt.getJavaSourceStartLineNumber, analyze(stmt))

  def isParameterInitStmt(expr: InvokeExpr, pmtCount: Int, unit: soot.Unit) : Boolean =
    unit.isInstanceOf[IdentityStmt] && unit.asInstanceOf[IdentityStmt].getRightOp.isInstanceOf[ParameterRef] && expr.getArg(pmtCount).isInstanceOf[Local]

  def isAssignReturnStmt(callSite: soot.Unit, unit: soot.Unit) : Boolean =
   unit.isInstanceOf[ReturnStmt] && unit.asInstanceOf[ReturnStmt].getOp.isInstanceOf[Local] &&
     callSite.isInstanceOf[soot.jimple.AssignStmt]

  /*
   * a class to visit the allocation nodes of the objects that
   * a field might point to.
   *
   * @param method method of the statement stmt
   * @param stmt statement with a load operation
   */
  class AllocationVisitor(val method: SootMethod, val stmt: soot.Unit) extends P2SetVisitor {
    override def visit(n: pag.Node): Unit = {
      if(n.isInstanceOf[AllocNode]) {
        val allocationNode = n.asInstanceOf[AllocNode]
        val body = allocationNode.getMethod.getActiveBody

        body.getUnits.stream().forEach(unit => {
          if(unit.isInstanceOf[soot.jimple.AssignStmt]) {
            val exp = unit.asInstanceOf[soot.jimple.AssignStmt].getRightOp
            if(exp.isInstanceOf[NewExpr] && allocationNode.getNewExpr == exp) {
              val source = createNode(allocationNode.getMethod, unit)
              val target = createNode(method, stmt)
              svg + source ~> target
            }
          }
          //TODO: should we also consider statments like return new ... or throw new ...?
        })
      }
    }
  }
}