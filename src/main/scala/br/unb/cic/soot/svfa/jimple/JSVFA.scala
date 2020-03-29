package br.unb.cic.soot.svfa.jimple

import java.util

import br.unb.cic.soot.graph.{Node, SinkNode, SourceNode}
import br.unb.cic.soot.svfa.{SVFA, SourceSinkDef}
import com.typesafe.scalalogging.LazyLogging
import soot.jimple._
import soot.jimple.spark.pag
import soot.jimple.spark.pag.{AllocNode, PAG}
import soot.jimple.spark.sets.{DoublePointsToSet, HybridPointsToSet, P2SetVisitor}
import soot.toolkits.graph.ExceptionalUnitGraph
import soot.toolkits.scalar.SimpleLocalDefs
import soot.{Local, Scene, SceneTransformer, SootMethod, Transform}

import scala.collection.mutable

/**
  * A Jimple based implementation of
  * SVFA.
  */
abstract class JSVFA extends SVFA with SourceSinkDef with LazyLogging {

  var methods = 0
  val traversedMethods = scala.collection.mutable.Set.empty[SootMethod]
  val allocationSites = scala.collection.mutable.HashMap.empty[NewExpr, soot.Unit]

  def createSceneTransform(): (String, Transform) = ("wjtp", new Transform("wjtp.svfa", new Transformer()))

  def configurePackages(): List[String] = List("cg", "wjtp")

  def beforeGraphConstruction(): Unit = { }

  def afterGraphConstruction() { }

  def initAllocationSites(): Unit = {
    val listener = Scene.v().getReachableMethods.listener()

    while(listener.hasNext) {
      val m = listener.next().method()
      if (m.hasActiveBody) {
        val body = m.getActiveBody

        body.getUnits.forEach(unit => {
          if (unit.isInstanceOf[soot.jimple.AssignStmt]) {
            val right = unit.asInstanceOf[soot.jimple.AssignStmt].getRightOp
            if (right.isInstanceOf[NewExpr]) {
              val exp = right.asInstanceOf[NewExpr]
              allocationSites += (exp -> unit)
            }
          }
        })
      }
    }
  }

  class Transformer extends SceneTransformer {
    override def internalTransform(phaseName: String, options: util.Map[String, String]): Unit = {
      pointsToAnalysis = Scene.v().getPointsToAnalysis

      initAllocationSites()

      Scene.v().getEntryPoints.forEach(method => {
        traverse(method)
        methods = methods + 1
      })
    }
  }

  def traverse(method: SootMethod) : Unit = {
    if((!method.hasActiveBody) || traversedMethods.contains(method)) {
      return
    }

    traversedMethods.add(method)

    val body  = method.retrieveActiveBody()

    logger.debug(body.toString)

    val graph = new ExceptionalUnitGraph(body)
    val defs  = new SimpleLocalDefs(graph)

    body.getUnits.forEach(unit => {
      val v = Statement.convert(unit)

      v match {
        case AssignStmt(base) => traverse(AssignStmt(base), method, defs)
        case InvokeStmt(base) => traverse(InvokeStmt(base), method, defs)
        case _ if(analyze(unit) == SinkNode) => traverseSinkStatement(v, method, defs)
        case _ =>
      }
    })
  }

  def traverse(assignStmt: AssignStmt, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val targetStmt = assignStmt.stmt
    targetStmt.getRightOp match {
      case exp : Local => copyRule(assignStmt.base, exp, method, defs)
      case exp : InvokeExpr => invokeRule(assignStmt, exp, method, defs)
      case exp : InstanceFieldRef => loadRule(assignStmt.base, exp, method, defs)
      case _ => // do nothing.
    }
  }

  def traverse(stmt: InvokeStmt, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val exp = stmt.stmt.getInvokeExpr
    invokeRule(stmt, exp, method, defs)
  }

  def traverseSinkStatement(statement: Statement, method: SootMethod, defs: SimpleLocalDefs): Unit = {
    statement.base.getUseBoxes.forEach(box => {
      box match {
        case local : Local => copyRule(statement.base, local, method, defs)
        case fieldRef : InstanceFieldRef => loadRule(statement.base, fieldRef, method, defs)
        case _ =>
        // TODO:
        //   we have to think about other cases here.
        //   e.g: a reference to a parameter
      }
    })
  }

  private def copyRule(targetStmt: soot.Unit, local: Local, method: SootMethod, defs: SimpleLocalDefs) = {
    defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
      val source = createNode(method, sourceStmt)
      val target = createNode(method, targetStmt)
      updateGraph(source, target)
    })
  }

  private def invokeRule(callStmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs): Unit = {
    val callee = exp.getMethod

    if(analyze(callStmt.base) == SinkNode) {
      defsToCallOfSinkMethod(callStmt, exp, caller, defs)
    }

    //TODO:
    //  Review the impact of this code here.
    //  Perhaps we should create edges between the
    //  call-site and the target method, even though
    //  the method does not have an active body.
    if(!callee.hasActiveBody) {
      if(analyze(callStmt.base) == SourceNode) {
        logger.info("================================")
        logger.info(" invoke expression of method without active body")
        logger.info("================================")

        svg.map(createNode(caller, callStmt.base)) = mutable.Set[Node]()
      }
      return
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

  private def loadRule(stmt: soot.Unit, ref: InstanceFieldRef, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val base = ref.getBase

    if(base.isInstanceOf[Local] && pointsToAnalysis.isInstanceOf[PAG]) {
      val pta = pointsToAnalysis.asInstanceOf[PAG]
      val allocations = pta.reachingObjects(base.asInstanceOf[Local], ref.getField).asInstanceOf[DoublePointsToSet].getNewSet
      allocations.asInstanceOf[HybridPointsToSet].forall(new AllocationVisitor(method, stmt))
    }
  }


  private def defsToCallSite(caller: SootMethod, callee: SootMethod, calleeDefs: SimpleLocalDefs, callStmt: soot.Unit, retStmt: soot.Unit) = {
    val local = retStmt.asInstanceOf[ReturnStmt].getOp.asInstanceOf[Local]
    calleeDefs.getDefsOfAt(local, retStmt).forEach(sourceStmt => {
      val source = createNode(callee, sourceStmt)
      val target = createNode(caller, callStmt)
      updateGraph(source, target)
    })
  }

  private def defsToFormalArgs(stmt: Statement, caller: SootMethod, defs: SimpleLocalDefs, assignStmt: soot.Unit, exp: InvokeExpr, callee: SootMethod, pmtCount: Int) = {
    val local = exp.getArg(pmtCount).asInstanceOf[Local]
    defs.getDefsOfAt(local, stmt.base).forEach(sourceStmt => {
      val source = createNode(caller, sourceStmt)
      val target = createNode(callee, assignStmt)
      updateGraph(source, target)
    })
  }

  private def defsToCallOfSinkMethod(stmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs) = {
    exp.getArgs.stream().filter(a => a.isInstanceOf[Local]).forEach(a => {
      val local = a.asInstanceOf[Local]
      val targetStmt = stmt.base
      defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
        val source = createNode(caller, sourceStmt)
        val target = createNode(caller, targetStmt)
        updateGraph(source, target)
      })
    })
  }

  /*
   * creates a graph note from a sootMethod / sootUnit
   */
  def createNode(method: SootMethod, stmt: soot.Unit): Node = Node(method.getDeclaringClass.toString, method.getSignature,
                       stmt.toString(), stmt.getJavaSourceStartLineNumber, analyze(stmt))

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

        if(allocationNode.getNewExpr.isInstanceOf[NewExpr]) {
          if(allocationSites.contains(allocationNode.getNewExpr.asInstanceOf[NewExpr])) {
            val unit = allocationSites(allocationNode.getNewExpr.asInstanceOf[NewExpr])

            val source = createNode(allocationNode.getMethod, unit)
            val target = createNode(method, stmt)
            updateGraph(source, target)

            // TODO: we have to discuss the strategy bellow
            svg.map.get(source).getOrElse(mutable.MutableList[Node]()).foreach(s => updateGraph(s, target))
          }
        }

//        body.getUnits.stream().forEach(unit => {
//          if(unit.isInstanceOf[soot.jimple.AssignStmt]) {
//            val exp = unit.asInstanceOf[soot.jimple.AssignStmt].getRightOp
//            if(exp.isInstanceOf[NewExpr] && allocationNode.getNewExpr == exp) {
//              val source = createNode(allocationNode.getMethod, unit)
//              val target = createNode(method, stmt)
//              svg + source ~> target
//            }
//          }
//        })
      }
    }
  }

  /**
   * Override this method in the case that
   * a complete graph should be generated.
   *
   * Otherwise, only nodes that can be reached from
   * source nodes will be in the graph
   *
   * @return true for a full sparse version of the graph.
   *         false otherwise.
   */
  def runInFullSparsenessMode() = true

  /*
   * It either updates the graph or not, depending on
   * the types of the nodes.
   */
  private def updateGraph(source: Node, target: Node): Unit = {
    if(!runInFullSparsenessMode()) {
      svg.addEdge(source, target)
      return
    }

    // this first case can still introduce irrelevant nodes
    if(svg.map.contains(source)) {//) || svg.map.contains(target)) {
      svg.addEdge(source, target)
    }
    else if(source.nodeType == SourceNode || source.nodeType == SinkNode) {
      svg.addEdge(source, target)
    }
    else if(target.nodeType == SourceNode || target.nodeType == SinkNode) {
      svg.addEdge(source, target)
    }
  }

}