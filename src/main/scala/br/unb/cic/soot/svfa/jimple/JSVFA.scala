package br.unb.cic.soot.svfa.jimple

import java.util
import br.unb.cic.soot.svfa.jimple.rules.RuleAction
import br.unb.cic.soot.graph.{CallSiteCloseLabel, CallSiteLabel, CallSiteOpenLabel, ContextSensitiveRegion, GraphNode, SinkNode, SourceNode, StatementNode}
import br.unb.cic.soot.svfa.jimple.dsl.{DSL, LanguageParser}
import br.unb.cic.soot.svfa.{SVFA, SourceSinkDef}
import com.typesafe.scalalogging.LazyLogging
import soot.jimple._
import soot.jimple.internal.{JArrayRef, JAssignStmt}
import soot.jimple.spark.ondemand.DemandCSPointsTo
import soot.jimple.spark.pag
import soot.jimple.spark.pag.{AllocNode, PAG}
import soot.jimple.spark.sets.{DoublePointsToSet, HybridPointsToSet, P2SetVisitor}
import soot.toolkits.graph.ExceptionalUnitGraph
import soot.toolkits.scalar.SimpleLocalDefs
import soot.{ArrayType, Local, Scene, SceneTransformer, SootField, SootMethod, Transform, Value, jimple}

import scala.collection.mutable.ListBuffer

/**
 * A Jimple based implementation of
 * SVFA.
 */
abstract class JSVFA extends SVFA with Analysis with FieldSensitiveness with ObjectPropagation with SourceSinkDef with LazyLogging  with DSL {

  var methods = 0
  val traversedMethods = scala.collection.mutable.Set.empty[SootMethod]
  val allocationSites = scala.collection.mutable.HashMap.empty[soot.Value, StatementNode]
  val arrayStores = scala.collection.mutable.HashMap.empty[Local, List[soot.Unit]]
  val languageParser = new LanguageParser(this)

  val methodRules = languageParser.evaluate(code())

  /*
   * Create an edge  from the definition of the local argument
   * to the definitions of the base object of a method call. In
   * more details, we should use this rule to address a situation
   * like:
   *
   * - virtualinvoke r3.<java.lang.StringBuffer: java.lang.StringBuffer append(java.lang.String)>(r1);
   *
   * Where we wanto create an edge from the definitions of r1 to
   * the definitions of r3.
   */
  trait CopyFromMethodArgumentToBaseObject extends RuleAction {
    def from: Int

    def apply(sootMethod: SootMethod, invokeStmt: jimple.Stmt, localDefs: SimpleLocalDefs) = {
      var srcArg: Value = null
      var expr: InvokeExpr = null

      try{
        srcArg = invokeStmt.getInvokeExpr.getArg(from)
        expr = invokeStmt.getInvokeExpr
      }catch {
        case e: Exception=>
          srcArg = invokeStmt.getInvokeExpr.getArg(from)
          expr = invokeStmt.getInvokeExpr
          println("Entrou com errro!")
      }
      if(hasBaseObject(expr) && srcArg.isInstanceOf[Local]) {
        val local = srcArg.asInstanceOf[Local]

        val base = getBaseObject(expr)

        if(base.isInstanceOf[Local]) {
          val localBase = base.asInstanceOf[Local]
          localDefs.getDefsOfAt(local, invokeStmt).forEach(sourceStmt => {
            val sourceNode = createNode(sootMethod, sourceStmt)
            localDefs.getDefsOfAt(localBase, invokeStmt).forEach(targetStmt =>{
              val targetNode = createNode(sootMethod, targetStmt)
              updateGraph(sourceNode, targetNode) // add comment
            })
          })
        }
      }
    }
  }

  private def getBaseObject(expr: InvokeExpr) =
    if (expr.isInstanceOf[VirtualInvokeExpr])
      expr.asInstanceOf[VirtualInvokeExpr].getBase
    else if(expr.isInstanceOf[SpecialInvokeExpr])
      expr.asInstanceOf[SpecialInvokeExpr].getBase
    else
      expr.asInstanceOf[InstanceInvokeExpr].getBase


  private def hasBaseObject(expr: InvokeExpr) =
    (expr.isInstanceOf[VirtualInvokeExpr] || expr.isInstanceOf[SpecialInvokeExpr] || expr.isInstanceOf[InterfaceInvokeExpr])


  /*
     * Create an edge from a method call to a local.
     * In more details, we should use this rule to address
     * a situation like:
     *
     * - $r6 = virtualinvoke r3.<java.lang.StringBuffer: java.lang.String toString()>();
     *
     * Where we want to create an edge from the definitions of r3 to
     * this statement.
     */
  trait CopyFromMethodCallToLocal extends RuleAction {
    def apply(sootMethod: SootMethod, invokeStmt: jimple.Stmt, localDefs: SimpleLocalDefs) = {
      val expr = invokeStmt.getInvokeExpr
      if(hasBaseObject(expr) && invokeStmt.isInstanceOf[jimple.AssignStmt]) {
        val base = getBaseObject(expr)
        val local = invokeStmt.asInstanceOf[jimple.AssignStmt].getLeftOp
        if(base.isInstanceOf[Local] && local.isInstanceOf[Local]) {
          val localBase = base.asInstanceOf[Local]
          localDefs.getDefsOfAt(localBase, invokeStmt).forEach(source => {
            val sourceNode = createNode(sootMethod, source)
            val targetNode = createNode(sootMethod, invokeStmt)
            updateGraph(sourceNode, targetNode) // add comment
          })
        }
      }
    }
  }


  /* Create an edge from the definitions of a local argument
   * to the assignment statement. In more details, we should use this rule to address
   * a situation like:
   * $r12 = virtualinvoke $r11.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>(r6);
   */
  trait CopyFromMethodArgumentToLocal extends RuleAction {
    def from: Int

    def apply(sootMethod: SootMethod, invokeStmt: jimple.Stmt, localDefs: SimpleLocalDefs) = {
      val srcArg = invokeStmt.getInvokeExpr.getArg(from)
      if(invokeStmt.isInstanceOf[JAssignStmt] && srcArg.isInstanceOf[Local]) {
        val local = srcArg.asInstanceOf[Local]
        val targetStmt = invokeStmt.asInstanceOf[jimple.AssignStmt]
        localDefs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
          val source = createNode(sootMethod, sourceStmt)
          val target = createNode(sootMethod, targetStmt)
          updateGraph(source, target) // add comment
        })
      }
    }
  }

  /*
 * Create an edge between the definitions of the actual
 * arguments of a method call. We should use this rule
 * to address situations like:
 *
 * - System.arraycopy(l1, _, l2, _)
 *
 * Where we wanto to create an edge from the definitions of
 * l1 to the definitions of l2.
 */
  trait CopyBetweenArgs extends RuleAction {
    def from: Int
    def target : Int

    def apply(sootMethod: SootMethod, invokeStmt: jimple.Stmt, localDefs: SimpleLocalDefs) = {
      val srcArg = invokeStmt.getInvokeExpr.getArg(from)
      val destArg = invokeStmt.getInvokeExpr.getArg(target)
      if (srcArg.isInstanceOf[Local] && destArg.isInstanceOf[Local]) {
        localDefs.getDefsOfAt(srcArg.asInstanceOf[Local], invokeStmt).forEach(sourceStmt => {
          val sourceNode = createNode(sootMethod, sourceStmt)
          localDefs.getDefsOfAt(destArg.asInstanceOf[Local], invokeStmt).forEach(targetStmt => {
            val targetNode = createNode(sootMethod, targetStmt)
            updateGraph(sourceNode, targetNode) // add comment
          })
        })
      }
    }
  }


  def createSceneTransform(): (String, Transform) = ("wjtp", new Transform("wjtp.svfa", new Transformer()))

  def initAllocationSites(): Unit = {
    val listener = Scene.v().getReachableMethods.listener()

    while(listener.hasNext) {
      val m = listener.next().method()
      if (m.hasActiveBody) {
        val body = m.getActiveBody
        body.getUnits.forEach(unit => {
          if (unit.isInstanceOf[soot.jimple.AssignStmt]) {
            val right = unit.asInstanceOf[soot.jimple.AssignStmt].getRightOp
            if (right.isInstanceOf[NewExpr] || right.isInstanceOf[NewArrayExpr] || right.isInstanceOf[StringConstant]) {
              allocationSites += (right -> createNode(m, unit))
            }
          }
          else if(unit.isInstanceOf[soot.jimple.ReturnStmt]) {
            val exp = unit.asInstanceOf[soot.jimple.ReturnStmt].getOp
            if(exp.isInstanceOf[StringConstant]) {
              allocationSites += (exp -> createNode(m, unit))
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

  def traverse(method: SootMethod, forceNewTraversal: Boolean = false) : Unit = {
    if((!forceNewTraversal) && (method.isPhantom || traversedMethods.contains(method))) {
      return
    }

    traversedMethods.add(method)

    val body  = method.retrieveActiveBody()

//    println(body)

    val graph = new ExceptionalUnitGraph(body)
    val defs  = new SimpleLocalDefs(graph)

    body.getUnits.forEach(unit => {
      val v = Statement.convert(unit)

      v match {
        case AssignStmt(base) => traverse(AssignStmt(base), method, defs)
        case InvokeStmt(base) => traverse(InvokeStmt(base), method, defs)
        case _ if analyze(unit) == SinkNode => traverseSinkStatement(v, method, defs)
        case _ =>
      }
    })
  }


  def traverse(assignStmt: AssignStmt, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val left = assignStmt.stmt.getLeftOp
    val right = assignStmt.stmt.getRightOp

    (left, right) match {
      case (p: Local, q: InstanceFieldRef) => loadRule(assignStmt.stmt, q, method, defs)
      case (p: Local, q: StaticFieldRef) => loadRule(assignStmt.stmt, q, method)
      case (p: Local, q: ArrayRef) => loadArrayRule(assignStmt.stmt, q, method, defs)
      case (p: Local, q: InvokeExpr) => invokeRule(assignStmt, q, method, defs) // call a method
      case (p: Local, q: Local) => copyRule(assignStmt.stmt, q, method, defs)
      case (p: Local, _) => copyRuleInvolvingExpressions(assignStmt.stmt, method, defs)
      case (p: InstanceFieldRef, _: Local) => storeRule(assignStmt.stmt, p, method, defs) // update 'edge' FROM stmt where right value was instanced TO current stmt
      case (_: StaticFieldRef, _: Local) => storeRule(assignStmt.stmt, method, defs)
      case (p: JArrayRef, _) => storeArrayRule(assignStmt)
      case _ =>
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


  private def invokeRule(callStmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs): Unit = {
    val callee = exp.getMethod

    if(analyze(callStmt.base) == SinkNode) {
      defsToCallOfSinkMethod(callStmt, exp, caller, defs) // update 'edge(s)' FROM "declaration stmt(s) for args" TO "call-site stmt" (current stmt)
      return  // TODO: we are not exploring the body of a sink method.
      //       For this reason, we only find one path in the
      //       FieldSample test case, instead of two.
    }

    if(analyze(callStmt.base) == SourceNode) {
      val source = createNode(caller, callStmt.base) // create a 'node' from stmt that calls the source method (call-site stmt)
      svg.addNode(source)
    }

    for(r <- methodRules) {
      if(r.check(callee)) {
        r.apply(caller, callStmt.base.asInstanceOf[jimple.Stmt], defs)
        return
      }
    }

    if(intraprocedural()) return

    var pmtCount = 0
    val body = callee.retrieveActiveBody()
    val g = new ExceptionalUnitGraph(body)
    val calleeDefs = new SimpleLocalDefs(g)

    body.getUnits.forEach(s => {
      if(isThisInitStmt(exp, s)) { // this := @this: className
        defsToThisObject(callStmt, caller, defs, s, exp, callee) // create 'Edge' FROM the stmt where the object that calls the method was instanced TO the this definition in callee method
      }
      else if(isParameterInitStmt(exp, pmtCount, s)) {  // <variable> := @parameter#: <variable-type>
        defsToFormalArgs(callStmt, caller, defs, s, exp, callee, pmtCount) // create an 'edge' FROM stmt(s) where the variable is defined TO stmt where the variable is loaded
        pmtCount = pmtCount + 1
      }
      else if(isAssignReturnLocalStmt(callStmt.base, s)) { // return "<variable>"
        defsToCallSite(caller, callee, calleeDefs, callStmt.base, s) // create an 'edge' FROM the stmt where the return variable is defined TO "call site stmt"
      }
      else if(isReturnStringStmt(callStmt.base, s)) { // return "<string>"
        stringToCallSite(caller, callee, callStmt.base, s) // create an 'edge' FROM "return string stmt" TO "call site stmt"
      }
    })

    traverse(callee)
  }

  private def applyPhantomMethodCallRule(callStmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs) = {
    val srcArg = exp.getArg(0)
    val destArg = exp.getArg(2)
    if (srcArg.isInstanceOf[Local] && destArg.isInstanceOf[Local]) {
      defs.getDefsOfAt(srcArg.asInstanceOf[Local], callStmt.base).forEach(srcArgDefStmt => {
        val sourceNode = createNode(caller, srcArgDefStmt)
        val allocationNodes = findAllocationSites(destArg.asInstanceOf[Local])
        allocationNodes.foreach(targetNode => {
          updateGraph(sourceNode, targetNode) // add comment
        })
      })
    }
  }

  /*
     * This rule deals with the following situation:
     *
     * (*) p = q
     *
     * In this case, we create an edge from defs(q)
     * to the statement p = q.
     */
  protected def copyRule(targetStmt: soot.Unit, local: Local, method: SootMethod, defs: SimpleLocalDefs) = {
    defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
      val source = createNode(method, sourceStmt)
      val target = createNode(method, targetStmt)
      updateGraph(source, target) // add comment
    })
  }

  /*
   * This rule deals with the following situation:
   *
   * (*) p = q + r
   *
   * In this case, we create and edge from defs(q) and
   * from defs(r) to the statement p = q + r
   */
  def copyRuleInvolvingExpressions(stmt: jimple.AssignStmt, method: SootMethod, defs: SimpleLocalDefs) = {
    stmt.getRightOp.getUseBoxes.forEach(box => {
      if(box.getValue.isInstanceOf[Local]) {
        val local = box.getValue.asInstanceOf[Local]
        copyRule(stmt, local, method, defs)
      }
    })
  }

  /*
   * This rule deals with the following situations:
   *
   *  (*) p = q.f
   */
  protected def loadRule(stmt: soot.Unit, ref: InstanceFieldRef, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val base = ref.getBase
    // value field of a string.
    val className = ref.getFieldRef.declaringClass().getName
    if((className == "java.lang.String") && ref.getFieldRef.name == "value") {
      if(base.isInstanceOf[Local]) {
        defs.getDefsOfAt(base.asInstanceOf[Local], stmt).forEach(source => {
          val sourceNode = createNode(method, source)
          val targetNode = createNode(method, stmt)
//          updateGraph(sourceNode, targetNode) // add comment
        })
      }
      return;
    }
    // default case
    if(base.isInstanceOf[Local]) {
      var allocationNodes = findFieldStores(base.asInstanceOf[Local], ref.getField)

      if (allocationNodes.isEmpty) {
        allocationNodes = findAllocationSites(base.asInstanceOf[Local], false, ref.getField)
      }

      if (allocationNodes.isEmpty) {
        allocationNodes = findAllocationSites(base.asInstanceOf[Local], true, ref.getField)
      }

      allocationNodes.foreach(source => {
        val target = createNode(method, stmt)
        updateGraph(source, target) // update 'edge' FROM allocationNode? stmt TO load rule stmt (current stmt)
        svg.getAdjacentNodes(source).get.foreach(s => {
            updateGraph(s, target) // update 'edge' FROM adjacent node of allocationNode? stmt TO load rule stmt (current stmt)
        }) // add comment
      })

      // create an edge from the base defs to target
      // if an object is tainted, we should propagate the taint to all
      // fields as well. Not completely sure if this should be
      // the case.
      if(propagateObjectTaint()) {
        defs.getDefsOfAt(base.asInstanceOf[Local], stmt).forEach(source => {
          val sourceNode = createNode(method, source)
          val targetNode = createNode(method, stmt)
          updateGraph(sourceNode, targetNode) // update 'edge' FROM stmt where object (that calls load attribute) is instanced TO load rule stmt (current stmt)
        })
      }
    }
  }

  /*
 * This rule deals with the following situation 
 * when "f" is an static variable (StaticFieldRef)
 *
 *  p = f
 */
  private def loadRule(stmt: soot.Unit, ref: StaticFieldRef, method: SootMethod) : Unit = {

      val findFieldStoresNodes = findFieldStores(ref) // find fields stores for StaticFieldRef

      findFieldStoresNodes.foreach(source => {
        val target = createNode(method, stmt)
        updateGraph(source, target) // update 'edge' FROM allocationNode? stmt TO load rule stmt (current stmt)
        svg.getAdjacentNodes(source).get.foreach(s => {
          updateGraph(s, target) // update 'edge' FROM adjacent node of allocationNode? stmt TO load rule stmt (current stmt)
        })
      })
  }

  protected def loadArrayRule(targetStmt: soot.Unit, ref: ArrayRef, method: SootMethod, defs: SimpleLocalDefs) : Unit = {
    val base = ref.getBase

    if(base.isInstanceOf[Local]) {
      val local = base.asInstanceOf[Local]

      defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
        val source = createNode(method, sourceStmt)
        val target = createNode(method, targetStmt)
        updateGraph(source, target) // add comment
      })

      val stores = arrayStores.getOrElseUpdate(local, List())
      stores.foreach(sourceStmt => {
        val source = createNode(method, sourceStmt)
        val target = createNode(method, targetStmt)
        updateGraph(source, target) // add comment
      })
    }
  }

  /*
   * This rule deals with statements in the form:
   *
   * (*) p.f = expression
   */

  /**
   * CASE 1
   * ??
   *
   * CASE 2
   * CREATE EDGE(S)
   * FROM stmt(s) where right value was instanced
   * TO: stmt load store stmt (current stmt)
   */
  private def storeRule(targetStmt: jimple.AssignStmt, fieldRef: InstanceFieldRef, method: SootMethod, defs: SimpleLocalDefs) = {
    val local = targetStmt.getRightOp.asInstanceOf[Local]
    if (fieldRef.getBase.isInstanceOf[Local]) {
      val base = fieldRef.getBase.asInstanceOf[Local]
      if (fieldRef.getField.getDeclaringClass.getName == "java.lang.String" && fieldRef.getField.getName == "value") {
        defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
          val source = createNode(method, sourceStmt)
          val allocationNodes = findAllocationSites(base)
          allocationNodes.foreach(targetNode => {
            updateGraph(source, targetNode) // add comment
          })
        })
      }
      else {
        // CASE 2
        //        val allocationNodes = findAllocationSites(base)

        //        val allocationNodes = findAllocationSites(base, true, fieldRef.getField)
        //        if(!allocationNodes.isEmpty) {
        //          allocationNodes.foreach(targetNode => {
        defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
          val source = createNode(method, sourceStmt)
          val target = createNode(method, targetStmt)
          updateGraph(source, target) // update 'edge' FROM stmt where right value was instanced TO current stmt
        })
        //          })
        //        }
      }
    }
  }

  /*
   * This rule deals with statements in the form:
   * when "p" is an static variable (StaticFieldRef)
   *
   * (*) p = expression
   *
   * This behavior is like a simple CopyRule, so that method is called here.
   */
  private def storeRule(stmt: jimple.AssignStmt, method: SootMethod, defs: SimpleLocalDefs) = {
    val local = stmt.getRightOp.asInstanceOf[Local]
    copyRule(stmt, local, method, defs)
  }

  def storeArrayRule(assignStmt: AssignStmt) {
    val l = assignStmt.stmt.getLeftOp.asInstanceOf[JArrayRef].getBase.asInstanceOf[Local]
    val stores = assignStmt.stmt :: arrayStores.getOrElseUpdate(l, List())
    arrayStores.put(l, stores)
  }

  /**
   * CASE 1
   *
   * Create EDGE(S)
   * "FROM" each stmt where the variable returned is defined.
   * "TO" call site stmt.
   *
   * An CS for "close" is added
   *  - Caller method
   *  - stmt from the callee method is called (stmt is in caller method)
   *  - Callee method
   *
   * CASE 2
   * ??
   */
  private def defsToCallSite(caller: SootMethod, callee: SootMethod, calleeDefs: SimpleLocalDefs, callStmt: soot.Unit, retStmt: soot.Unit) = {

    // CASE 1
    val target = createNode(caller, callStmt)
    val local = retStmt.asInstanceOf[ReturnStmt].getOp.asInstanceOf[Local]
    calleeDefs.getDefsOfAt(local, retStmt).forEach(sourceStmt => {
      val source = createNode(callee, sourceStmt)
      val csCloseLabel = createCSCloseLabel(caller, callStmt, callee)
      svg.addEdge(source, target, csCloseLabel) // create an EDGE FROM "definition stmt from return variable " TO "call site stmt"
      
      // CASE 2
      if(local.getType.isInstanceOf[ArrayType]) {
        val stores = arrayStores.getOrElseUpdate(local, List())
        stores.foreach(sourceStmt => {
          val source = createNode(callee, sourceStmt)
          val csCloseLabel = createCSCloseLabel(caller, callStmt, callee)
          svg.addEdge(source, target, csCloseLabel) // add comment
        })
      }
    })
  }

  /**
   * CREATE EDGE
   * FROM: return stmt.
   * TO: call site stmt.
   */
  private def stringToCallSite(caller: SootMethod, callee: SootMethod, callStmt: soot.Unit, retStmt: soot.Unit): Unit = {
    val target = createNode(caller, callStmt)
    val source = createNode(callee, retStmt)
    svg.addEdge(source, target) // create an 'edge' FROM "return stmt " TO "call site stmt"
  }

  /**
   * CREATE EDGE(s)
   * FROM: stmt where the object that calls the method was instanced
   * TO: the 'this' definition in constructor (init) from callee method
   *
   * OPEN CS:
   */
  private def defsToThisObject(callStatement: Statement, caller: SootMethod, calleeDefs: SimpleLocalDefs, targetStmt: soot.Unit, expr: InvokeExpr, callee: SootMethod) : Unit = {

    // Check if the current expression belong to invokeExpr
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
          val csOpenLabel = createCSOpenLabel(caller, callStatement.base, callee)
          svg.addEdge(source, target, csOpenLabel) // create 'Edge' FROM the stmt where the object that calls the method was instanced TO the this definition in callee method
        })
      }
    }
  }

  /**
   * Create EDGE(S)
   * "FROM" each stmt where the variable, passed as a parameter, is defined.
   * "TO" stmt where the variable is loaded inside the method.
   *
   * An "open" CS is added
   *  - Caller method
   *  - stmt where the callee method is called (it is in caller method)
   *  - Callee method
   */
  private def defsToFormalArgs(stmt: Statement, caller: SootMethod, defs: SimpleLocalDefs, assignStmt: soot.Unit, exp: InvokeExpr, callee: SootMethod, pmtCount: Int) = {
    val target = createNode(callee, assignStmt)

    val local = exp.getArg(pmtCount).asInstanceOf[Local]
    defs.getDefsOfAt(local, stmt.base).forEach(sourceStmt => {
      val source = createNode(caller, sourceStmt)
      val csOpenLabel = createCSOpenLabel(caller, stmt.base, callee) //
      svg.addEdge(source, target, csOpenLabel) // creates an 'edge' FROM stmt where the variable is defined TO stmt where the variable is loaded
    })
  }

  /**
   * CASE 1:
   * UPDATE EDGE(S)
   * "FROM" each stmt where the variable, passed as an argument, is defined.
   * "TO" stmt where the method is called (call-site stmt).
   *
   * CASE 2:
   * ???
   *
   * CASE 2:
   * ???
   */
  private def defsToCallOfSinkMethod(stmt: Statement, exp: InvokeExpr, caller: SootMethod, defs: SimpleLocalDefs) = {

    // CASE 1
    exp.getArgs.stream().filter(a => a.isInstanceOf[Local]).forEach(a => {
      val local = a.asInstanceOf[Local]
      val targetStmt = stmt.base
      defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
        val source = createNode(caller, sourceStmt)
        val target = createNode(caller, targetStmt)
        updateGraph(source, target) // update 'edge(s)' FROM "declaration stmt(s) for args" TO "call-site stmt" (current stmt)
      })

      //CASE 2
      if(local.getType.isInstanceOf[ArrayType]) {
        val stores = arrayStores.getOrElseUpdate(local, List())
        stores.foreach(sourceStmt => {
          val source = createNode(caller, sourceStmt)
          val target = createNode(caller, targetStmt)
          updateGraph(source, target) // add comment
        })
      }
    })

    // CASE 3
    // edges from definition to base object of an invoke expression
    if(isFieldSensitiveAnalysis() && exp.isInstanceOf[InstanceInvokeExpr]) {
      if(exp.asInstanceOf[InstanceInvokeExpr].getBase.isInstanceOf[Local]) {
        val local = exp.asInstanceOf[InstanceInvokeExpr].getBase.asInstanceOf[Local]
        val targetStmt = stmt.base
        defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
          val source = createNode(caller, sourceStmt)
          val target = createNode(caller, targetStmt)
          updateGraph(source, target) // add comment
        })
      }
    }
  }

  /*
   * creates a graph node from a sootMethod / sootUnit
   */
  def createNode(method: SootMethod, stmt: soot.Unit): StatementNode =
    svg.createNode(method, stmt, analyze)


  def createCSOpenLabel(method: SootMethod, stmt: soot.Unit, callee: SootMethod): CallSiteLabel = {
    val statement = br.unb.cic.soot.graph.Statement(method.getDeclaringClass.toString, method.getSignature, stmt.toString,
      stmt.getJavaSourceStartLineNumber, stmt, method)
    CallSiteLabel(ContextSensitiveRegion(statement, callee.toString), CallSiteOpenLabel)
  }

  def createCSCloseLabel(method: SootMethod, stmt: soot.Unit, callee: SootMethod): CallSiteLabel = {
    val statement = br.unb.cic.soot.graph.Statement(method.getDeclaringClass.toString, method.getSignature, stmt.toString,
      stmt.getJavaSourceStartLineNumber, stmt, method)
    CallSiteLabel(ContextSensitiveRegion(statement, callee.toString), CallSiteCloseLabel)
  }

  def isThisInitStmt(expr: InvokeExpr, unit: soot.Unit) : Boolean =
    unit.isInstanceOf[IdentityStmt] && unit.asInstanceOf[IdentityStmt].getRightOp.isInstanceOf[ThisRef]

  def isParameterInitStmt(expr: InvokeExpr, pmtCount: Int, unit: soot.Unit) : Boolean =
    unit.isInstanceOf[IdentityStmt] && unit.asInstanceOf[IdentityStmt].getRightOp.isInstanceOf[ParameterRef] && expr.getArg(pmtCount).isInstanceOf[Local]

  def isAssignReturnLocalStmt(callSite: soot.Unit, unit: soot.Unit) : Boolean =
    unit.isInstanceOf[ReturnStmt] && unit.asInstanceOf[ReturnStmt].getOp.isInstanceOf[Local] &&
      callSite.isInstanceOf[soot.jimple.AssignStmt]

  def isReturnStringStmt(callSite: soot.Unit, unit: soot.Unit): Boolean =
    unit.isInstanceOf[ReturnStmt] && unit.asInstanceOf[ReturnStmt].getOp.isInstanceOf[StringConstant] &&
      callSite.isInstanceOf[soot.jimple.AssignStmt]

  def findAllocationSites(local: Local, oldSet: Boolean = true, field: SootField = null) : ListBuffer[GraphNode] = {
    val pta = if(pointsToAnalysis.isInstanceOf[PAG]) pointsToAnalysis.asInstanceOf[PAG]
    else if (pointsToAnalysis.isInstanceOf[DemandCSPointsTo]) pointsToAnalysis.asInstanceOf[DemandCSPointsTo].getPAG
    else null

    if(pta != null) {
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
    new ListBuffer[GraphNode]()
  }

  /*
   * a class to visit the allocation nodes of the objects that
   * a field might point to.
   *
   * @param method method of the statement stmt
   * @param stmt statement with a load operation
   */
  class AllocationVisitor() extends P2SetVisitor {

    var allocationNodes = new ListBuffer[GraphNode]()

    override def visit(n: pag.Node): Unit = {
      if (n.isInstanceOf[AllocNode]) {
        val allocationNode = n.asInstanceOf[AllocNode]

        var stmt : StatementNode = null

        if (allocationNode.getNewExpr.isInstanceOf[NewExpr]) {
          if (allocationSites.contains(allocationNode.getNewExpr.asInstanceOf[NewExpr])) {
            stmt = allocationSites(allocationNode.getNewExpr.asInstanceOf[NewExpr])
          }
        }
        else if(allocationNode.getNewExpr.isInstanceOf[NewArrayExpr]) {
          if (allocationSites.contains(allocationNode.getNewExpr.asInstanceOf[NewArrayExpr])) {
            stmt = allocationSites(allocationNode.getNewExpr.asInstanceOf[NewArrayExpr])
          }
        }
        else if(allocationNode.getNewExpr.isInstanceOf[String]) {
          val str: StringConstant = StringConstant.v(allocationNode.getNewExpr.asInstanceOf[String])
          stmt = allocationSites.getOrElseUpdate(str, null)
        }

        if(stmt != null) {
          allocationNodes += stmt
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

  def findFieldStores(local: Local, field: SootField) : ListBuffer[GraphNode] = {
    val res: ListBuffer[GraphNode] = new ListBuffer[GraphNode]()
    for(node <- svg.nodes()) {
      if(node.unit().isInstanceOf[soot.jimple.AssignStmt]) {
        val assignment = node.unit().asInstanceOf[soot.jimple.AssignStmt]
        if(assignment.getLeftOp.isInstanceOf[InstanceFieldRef]) {
          val base = assignment.getLeftOp.asInstanceOf[InstanceFieldRef].getBase.asInstanceOf[Local]
          if(pointsToAnalysis.reachingObjects(base).hasNonEmptyIntersection(pointsToAnalysis.reachingObjects(local)) || areThisFromSameClass(base, local)) {
            if(field.equals(assignment.getLeftOp.asInstanceOf[InstanceFieldRef].getField)) {
              res += createNode(node.method(), node.unit())
            }
          }
        }
      }
    }
    return res
  }

  // findFieldStores for static variables
  private def findFieldStores(field: StaticFieldRef) : ListBuffer[GraphNode] = {
    val res: ListBuffer[GraphNode] = new ListBuffer[GraphNode]()
    for(node <- svg.nodes()) {
      if(node.unit().isInstanceOf[soot.jimple.AssignStmt]) {
        val assignment = node.unit().asInstanceOf[soot.jimple.AssignStmt]
        if(assignment.getLeftOp.isInstanceOf[StaticFieldRef]) {
          val base = assignment.getLeftOp.asInstanceOf[StaticFieldRef]
            if(field.getFieldRef.equals(base.getFieldRef)) {
              res += createNode(node.method(), node.unit())
            }
        }
      }
    }
    return res
  }

  private def areThisFromSameClass(base: Local, local: Local): Boolean = {
    base.getName == local.getName && base.getType == local.getType && base.getName.equals("this")
  }

  //  /*
  //   * It either updates the graph or not, depending on
  //   * the types of the nodes.
  //   */

  def containsNodeDF(node: StatementNode): StatementNode = {
    for (n <- svg.edges()){
      var auxNodeFrom = n.from.asInstanceOf[StatementNode]
      var auxNodeTo = n.to.asInstanceOf[StatementNode]
      if (auxNodeFrom.equals(node)) return n.from.asInstanceOf[StatementNode]
      if (auxNodeTo.equals(node)) return n.to.asInstanceOf[StatementNode]
    }
    return null
  }
  def updateGraph(source: GraphNode, target: GraphNode, forceNewEdge: Boolean = false): Boolean = {
    var res = false
    if(!runInFullSparsenessMode() || true) {
      addNodeAndEdgeDF(source.asInstanceOf[StatementNode], target.asInstanceOf[StatementNode])

      res = true
    }
    return res
  }

  def addNodeAndEdgeDF(from: StatementNode, to: StatementNode): Unit = {
    var auxNodeFrom = containsNodeDF(from)
    var auxNodeTo = containsNodeDF(to)
    if (auxNodeFrom != null){
      if (auxNodeTo != null){
        svg.addEdge(auxNodeFrom, auxNodeTo)
      }else{
        svg.addEdge(auxNodeFrom, to)
      }
    }else {
      if (auxNodeTo != null) {
        svg.addEdge(from, auxNodeTo)
      } else {
        svg.addEdge(from, to)
      }
    }
  }

}
