package br.unb.cic.soot.svfa

import java.util

import br.unb.cic.soot.graph.{Node, SinkNode}
import scalax.collection.GraphPredef._
import boomerang.callgraph.ObservableDynamicICFG
import boomerang.preanalysis.BoomerangPretransformer
import br.unb.cic.soot.boomerang.Solver
import br.unb.cic.soot.jimple.{AssignStmt, InvokeStmt, Statement}
import soot.jimple.{IdentityStmt, InstanceFieldRef, InvokeExpr, ParameterRef, ReturnStmt}
import soot.toolkits.graph.ExceptionalUnitGraph
import soot.toolkits.scalar.SimpleLocalDefs
import soot.{Local, Scene, SceneTransformer, SootMethod, Transform, jimple}

/**
  * A Jimple based implementation of
  * SVFA.
  */
abstract class JSVFA extends SVFA with StmtAnalyzer {

  var solver : Solver = _
  var observableDynamicICFG : ObservableDynamicICFG = _
  var methods = 0

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
    val queries = solver.findAllocationSites(method, stmt.base)
    queries.foreach(q => {
      val stmts = solver.findDefinitions(ref, q, pointsToAnalysis)
      stmts.foreach(s => {
        val source = createNode(s.getMethod, s.getUnit.get())
        val target = createNode(method, stmt.base)
        svg += source ~> target
      })
    })
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
}