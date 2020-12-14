package br.unb.cic.soot.svfa.jimple

import java.util

import br.unb.cic.soot.graph.{CallSiteCloseEdge, CallSiteData, CallSiteOpenEdge, DefaultCallSiteLabel, LambdaNode, SimpleNode, SinkNode, SourceNode, Stmt, StmtNode}
import br.unb.cic.soot.svfa.{SVFA, SourceSinkDef}
import com.typesafe.scalalogging.LazyLogging
import soot.jimple._
import soot.jimple.internal.JArrayRef
import soot.jimple.spark.pag
import soot.jimple.spark.pag.{AllocNode, PAG}
import soot.jimple.spark.sets.{DoublePointsToSet, HybridPointsToSet, P2SetVisitor}
import soot.toolkits.graph.ExceptionalUnitGraph
import soot.toolkits.scalar.SimpleLocalDefs
import soot.{ArrayType, Local, Scene, SceneTransformer, SootField, SootMethod, Transform, jimple}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * A Jimple based implementation of
  * SVFA.
  */
abstract class JSVFA extends SVFA with Analysis with FieldSensitiveness with SourceSinkDef with LazyLogging {

  var methods = 0
  val traversedMethods = scala.collection.mutable.Set.empty[SootMethod]
  val allocationSites = scala.collection.mutable.HashMap.empty[soot.Value, soot.Unit]
  val arrayStores = scala.collection.mutable.HashMap.empty[Local, List[soot.Unit]]

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
            if (right.isInstanceOf[NewExpr] || right.isInstanceOf[NewArrayExpr]) {// || right.isInstanceOf[StringConstant]) {
              allocationSites += (right -> unit)
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

  def traverse(method: SootMethod, forseNewTraversal: Boolean = false) : Unit = {
    if((!forseNewTraversal) && (method.isPhantom || traversedMethods.contains(method))) {
      return
    }

    traversedMethods.add(method)

    val body  = method.retrieveActiveBody()

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
    val left = assignStmt.stmt.getLeftOp
    val right = assignStmt.stmt.getRightOp

    (left, right) match {
      case (p: Local, q: InstanceFieldRef) => loadRule(assignStmt.stmt, q, method, defs)
      case (p: Local, q: ArrayRef) => loadArrayRule(assignStmt.stmt, q, method, defs)
      case (p: Local, q: InvokeExpr) => invokeRule(assignStmt, q, method, defs)
      case (p: Local, q: Local) => copyRule(assignStmt.stmt, q, method, defs)
      case (p: Local, _) => copyRuleInvolvingExpressions(assignStmt.stmt, method, defs)
      case (p: InstanceFieldRef, q: Local) => storeRule(assignStmt.stmt, method, defs)
      case (p: JArrayRef, _) => storeArrayRule(assignStmt)
      case _ =>
    }
  }


  private def storeRule(targetStmt: jimple.AssignStmt, method: SootMethod, defs: SimpleLocalDefs) = {
    val local = targetStmt.getRightOp.asInstanceOf[Local]
    val fieldRef = targetStmt.getLeftOp.asInstanceOf[InstanceFieldRef]
    if (fieldRef.getBase.isInstanceOf[Local]) {
      val base = fieldRef.getBase.asInstanceOf[Local]
      if (fieldRef.getField.getDeclaringClass.getName == "java.lang.String" && fieldRef.getField.getName == "value") {
        defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
          val source = createNode(method, sourceStmt)
          val allocationNodes = findAllocationSites(base)
          allocationNodes.foreach(targetNode => {
            updateGraph(source, targetNode)
          })
        })
      }
      else {
//        val allocationNodes = findAllocationSites(base, true, fieldRef.getField)
//        if(!allocationNodes.isEmpty) {
          //allocationNodes.foreach(targetNode => {
            defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
              val source = createNode(method, sourceStmt)
              val target = createNode(method, targetStmt)
              updateGraph(source, target)
            })
          //})
        //}
      }
    }
  }

  def storeArrayRule(assignStmt: AssignStmt) {
    val l = assignStmt.stmt.getLeftOp.asInstanceOf[JArrayRef].getBase.asInstanceOf[Local]
    val stores = assignStmt.stmt :: arrayStores.getOrElseUpdate(l, List())
    arrayStores.put(l, stores)
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

    logger.debug(exp.getMethod.toString + callStmt.base.getJavaSourceStartLineNumber.toString())

    if(analyze(callStmt.base) == SinkNode) {
      defsToCallOfSinkMethod(callStmt, exp, caller, defs)
      // TODO: Is really necessary analyse the sink method
       return
    }

    if(analyze(callStmt.base) == SourceNode) {
      val source = createNode(caller, callStmt.base)
      svg.addNode(source)
    }

    //TODO:
    //  Review the impact of this code here.
    //  Perhaps we should create edges between the
    //  call-site and the target method, even though
    //  the method does not have an active body.
    if(callee.isPhantom || (!callee.hasActiveBody && callee.getSource == null)) {
      if(callee.getDeclaringClass.getName == "java.lang.System" && callee.getName == "arraycopy") {
        val srcArg = exp.getArg(0)
        val destArg = exp.getArg(2)
        if(srcArg.isInstanceOf[Local] && destArg.isInstanceOf[Local]) {
          defs.getDefsOfAt(srcArg.asInstanceOf[Local], callStmt.base).forEach(srcArgDefStmt => {
            val sourceNode = createNode(caller, srcArgDefStmt)
            val allocationNodes = findAllocationSites(destArg.asInstanceOf[Local])
            allocationNodes.foreach(targetNode => {
              updateGraph(sourceNode, targetNode)
            })
          })
        }
      }
      return
    }

    if(intraprocedural()) return

    var pmtCount = 0
    val body = callee.retrieveActiveBody()
    val g = new ExceptionalUnitGraph(body)
    val calleeDefs = new SimpleLocalDefs(g)

    body.getUnits.forEach(s => {
      if(isThisInitStmt(exp, s)) {
        defsToThisObject(callStmt, caller, defs, s, exp, callee)
      }
      else if(isParameterInitStmt(exp, pmtCount, s)) {
        defsToFormalArgs(callStmt, caller, defs, s, exp, callee, pmtCount)
        pmtCount = pmtCount + 1
      }
      else if(isAssignReturnStmt(callStmt.base, s)) {
        defsToCallSite(caller, callee, calleeDefs, callStmt.base, s)
      }
    })

    traverse(callee)
  }

  def copyRuleInvolvingExpressions(stmt: jimple.AssignStmt, method: SootMethod, defs: SimpleLocalDefs) = {
    stmt.getRightOp.getUseBoxes.forEach(box => {
      if(box.getValue.isInstanceOf[Local]) {
        val local = box.getValue.asInstanceOf[Local]
        copyRule(stmt, local, method, defs)
      }
    })
  }

  private def loadRule(stmt: soot.Unit, ref: InstanceFieldRef, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val base = ref.getBase
    // value field of a string.
    val className = ref.getFieldRef.declaringClass().getName
    if((className == "java.lang.String") && ref.getFieldRef.name == "value") {
      if(base.isInstanceOf[Local]) {
        defs.getDefsOfAt(base.asInstanceOf[Local], stmt).forEach(source => {
          val sourceNode = createNode(method, source)
          val targetNode = createNode(method, stmt)
          updateGraph(sourceNode, targetNode)
        })
      }
      return;
    }
    // default case
    if(base.isInstanceOf[Local]) {
      val allocationNodes = findAllocationSites(base.asInstanceOf[Local], false, ref.getField)
      allocationNodes.foreach(source => {
        val target = createNode(method, stmt)
        updateGraph(source, target)
        svg.getAdjacentNodes(source).get.foreach(s => updateGraph(s, target))
      })
    }
  }

  private def loadArrayRule(targetStmt: soot.Unit, ref: ArrayRef, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val base = ref.getBase
    logger.info(base.getClass.toString)
    if(base.isInstanceOf[Local]) {
      val local = base.asInstanceOf[Local]
      // an edge for each full assignment of the array to
      // this target statement.

      defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
        val source = createNode(method, sourceStmt)
        val target = createNode(method, targetStmt)
        updateGraph(source, target)
      })

      // and edge for each assignment to a single position
      // of the array, to this target assignment.
      val stores = arrayStores.getOrElseUpdate(local, List())
      stores.foreach(sourceStmt => {
        val source = createNode(method, sourceStmt)
        val target = createNode(method, targetStmt)
        updateGraph(source, target)
      })
    }
  }

  private def defsToCallSite(caller: SootMethod, callee: SootMethod, calleeDefs: SimpleLocalDefs, callStmt: soot.Unit, retStmt: soot.Unit) = {
    val target = createNode(caller, callStmt)

    val local = retStmt.asInstanceOf[ReturnStmt].getOp.asInstanceOf[Local]
    calleeDefs.getDefsOfAt(local, retStmt).forEach(sourceStmt => {
      val source = createNode(callee, sourceStmt)
      val csCloseLabel = createCSCloseLabel(caller, callStmt, sourceStmt, callee)
      svg.addEdge(source, target, csCloseLabel)


      if(local.getType.isInstanceOf[ArrayType]) {
        val stores = arrayStores.getOrElseUpdate(local, List())
        stores.foreach(sourceStmt => {
          val source = createNode(callee, sourceStmt)
          val csCloseLabel = createCSCloseLabel(caller, callStmt, sourceStmt, callee)
          svg.addEdge(source, target, csCloseLabel)
        })
      }
    })
  }

  private def defsToThisObject(callStatement: Statement, caller: SootMethod, calleeDefs: SimpleLocalDefs, targetStmt: soot.Unit, expr: InvokeExpr, callee: SootMethod) : Unit = {
    val invokeExpr = expr match {
      case e: VirtualInvokeExpr => e
      case e: SpecialInvokeExpr => e
      case e: InterfaceInvokeExpr => e
      case _ => null //TODO: not sure if the other cases
                     // are also relevant here. Otherwise,
                     // we can just match with InstanceInvokeExpr
    }

    if(invokeExpr != null) {
      if(invokeExpr.getBase.isInstanceOf[Local]) {

        val target = createNode(callee, targetStmt)

        val base = invokeExpr.getBase.asInstanceOf[Local]
        calleeDefs.getDefsOfAt(base, callStatement.base).forEach(sourceStmt => {
          val source = createNode(caller, sourceStmt)
          val csOpenLabel = createCSOpenLabel(caller, callStatement.base, sourceStmt, callee)
          svg.addEdge(source, target, csOpenLabel)
        })
      }
    }
  }

  private def defsToFormalArgs(stmt: Statement, caller: SootMethod, defs: SimpleLocalDefs, assignStmt: soot.Unit, exp: InvokeExpr, callee: SootMethod, pmtCount: Int) = {
    val target = createNode(callee, assignStmt)

    val local = exp.getArg(pmtCount).asInstanceOf[Local]
    defs.getDefsOfAt(local, stmt.base).forEach(sourceStmt => {
      val source = createNode(caller, sourceStmt)
      val csOpenLabel = createCSOpenLabel(caller, stmt.base, sourceStmt, callee)
      svg.addEdge(source, target, csOpenLabel)
    })
  }

  private def defsToCallOfSinkMethod(stmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs) = {
    // edges from definitions to args
    exp.getArgs.stream().filter(a => a.isInstanceOf[Local]).forEach(a => {
      val local = a.asInstanceOf[Local]
      val targetStmt = stmt.base
      defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
        val source = createNode(caller, sourceStmt)
        val target = createNode(caller, targetStmt)
        updateGraph(source, target)
      })

      if(local.getType.isInstanceOf[ArrayType]) {
        val stores = arrayStores.getOrElseUpdate(local, List())
        stores.foreach(sourceStmt => {
          val source = createNode(caller, sourceStmt)
          val target = createNode(caller, targetStmt)
          updateGraph(source, target)
        })
      }
    })
    // edges from definition to base object of an invoke expression
    if(isFieldSensitiveAnalysis() && exp.isInstanceOf[InstanceInvokeExpr]) {
      if(exp.asInstanceOf[InstanceInvokeExpr].getBase.isInstanceOf[Local]) {
        val local = exp.asInstanceOf[InstanceInvokeExpr].getBase.asInstanceOf[Local]
        val targetStmt = stmt.base
        defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
          val source = createNode(caller, sourceStmt)
          val target = createNode(caller, targetStmt)
          updateGraph(source, target)
        })
      }
    }
  }

  /*
   * creates a graph node from a sootMethod / sootUnit
   */
  def createNode(method: SootMethod, stmt: soot.Unit): StmtNode =
    new StmtNode(Stmt(method.getDeclaringClass.toString, method.getSignature, stmt.toString, stmt.getJavaSourceStartLineNumber), analyze(stmt))

  def createCSOpenLabel(method: SootMethod, stmt: soot.Unit, sourceStmt: soot.Unit, callee: SootMethod): DefaultCallSiteLabel =
    new DefaultCallSiteLabel(CallSiteData(method.getDeclaringClass.toString, method.getSignature,
      stmt.toString, stmt.getJavaSourceStartLineNumber, sourceStmt.toString, callee.toString, CallSiteOpenEdge))

  def createCSCloseLabel(method: SootMethod, stmt: soot.Unit, sourceStmt: soot.Unit, callee: SootMethod): DefaultCallSiteLabel =
    new DefaultCallSiteLabel(CallSiteData(method.getDeclaringClass.toString, method.getSignature,
      stmt.toString, stmt.getJavaSourceStartLineNumber, sourceStmt.toString, callee.toString, CallSiteCloseEdge))

  def isThisInitStmt(expr: InvokeExpr, unit: soot.Unit) : Boolean =
    unit.isInstanceOf[IdentityStmt] && unit.asInstanceOf[IdentityStmt].getRightOp.isInstanceOf[ThisRef]

  def isParameterInitStmt(expr: InvokeExpr, pmtCount: Int, unit: soot.Unit) : Boolean =
    unit.isInstanceOf[IdentityStmt] && unit.asInstanceOf[IdentityStmt].getRightOp.isInstanceOf[ParameterRef] && expr.getArg(pmtCount).isInstanceOf[Local]

  def isAssignReturnStmt(callSite: soot.Unit, unit: soot.Unit) : Boolean =
   unit.isInstanceOf[ReturnStmt] && unit.asInstanceOf[ReturnStmt].getOp.isInstanceOf[Local] &&
     callSite.isInstanceOf[soot.jimple.AssignStmt]

  def findAllocationSites(local: Local, oldSet: Boolean = true, field: SootField = null) : ListBuffer[LambdaNode] = {
    if(pointsToAnalysis.isInstanceOf[PAG]) {
      val pta = pointsToAnalysis.asInstanceOf[PAG]

      val reachingObjects = if(field == null) pta.reachingObjects(local.asInstanceOf[Local])
                            else pta.reachingObjects(local, field)

      if(!reachingObjects.isEmpty) {
        val allocations = if(oldSet) reachingObjects.asInstanceOf[DoublePointsToSet].getOldSet
                          else reachingObjects.asInstanceOf[DoublePointsToSet].getNewSet

        val v = new AllocationVisitor()
        allocations.asInstanceOf[HybridPointsToSet].forall(v)
        return v.allocationNodes
      }
    }
    new ListBuffer[LambdaNode]()
  }

  /*
   * a class to visit the allocation nodes of the objects that
   * a field might point to.
   *
   * @param method method of the statement stmt
   * @param stmt statement with a load operation
   */
  class AllocationVisitor() extends P2SetVisitor {
    var allocationNodes = new ListBuffer[LambdaNode]()
    override def visit(n: pag.Node): Unit = {
      if (n.isInstanceOf[AllocNode]) {
        val allocationNode = n.asInstanceOf[AllocNode]

        var unit: soot.Unit = null

        if (allocationNode.getNewExpr.isInstanceOf[NewExpr]) {
          if (allocationSites.contains(allocationNode.getNewExpr.asInstanceOf[NewExpr])) {
            unit = allocationSites(allocationNode.getNewExpr.asInstanceOf[NewExpr])
          }
        }
        else if(allocationNode.getNewExpr.isInstanceOf[NewArrayExpr]) {
          if (allocationSites.contains(allocationNode.getNewExpr.asInstanceOf[NewArrayExpr])) {
            unit = allocationSites(allocationNode.getNewExpr.asInstanceOf[NewArrayExpr])
          }
        }

        if(unit != null) {
          allocationNodes += createNode(allocationNode.getMethod, unit)
        }
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
   * @deprecated
   */
   def runInFullSparsenessMode() = true

//  /*
//   * It either updates the graph or not, depending on
//   * the types of the nodes.
//   */
  private def updateGraph(source: LambdaNode, target: LambdaNode, forceNewEdge: Boolean = false): Boolean = {
    var res = false
    if(!runInFullSparsenessMode() || true) {
      svg.addEdge(source, target)
      res = true
    }

    // this first case can still introduce irrelevant nodes
//    if(svg.contains(source)) {//) || svg.map.contains(target)) {
//      svg.addEdge(source, target)
//      res = true
//    }
//    else if(source.nodeType == SourceNode || source.nodeType == SinkNode) {
//      svg.addEdge(source, target)
//      res = true
//    }
//    else if(target.nodeType == SourceNode || target.nodeType == SinkNode) {
//      svg.addEdge(source, target)
//      res = true
//    }
    return res
  }
}
