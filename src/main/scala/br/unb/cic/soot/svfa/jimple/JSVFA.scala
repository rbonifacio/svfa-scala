package br.unb.cic.soot.svfa.jimple

import java.util
import soot.toolkits.graph._
import br.unb.cic.soot.graph.{CallSite, CallSiteCloseEdge, CallSiteLabel, CallSiteOpenEdge, ControlDependency, ControlDependencyFalse, LambdaNode, SimpleNode, SinkNode, SourceNode, Stmt, StmtNode, StringLabel}
import br.unb.cic.soot.svfa.{SVFA, SourceSinkDef}
import br.unb.cic.soot.svfa.rules.ArrayCopyRule
import com.typesafe.scalalogging.LazyLogging
import soot.jimple.GotoStmt
import soot.toolkits.graph.ExceptionalBlockGraph
import soot.toolkits.scalar.SimpleLocalDefs
import soot.{Local, Scene, SceneTransformer, SootMethod, Transform}

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
  val phantomMethodRules = List(new ArrayCopyRule)
  def createSceneTransform(): (String, Transform) = ("wjtp", new Transform("wjtp.svfa", new Transformer()))

  def configurePackages(): List[String] = List("cg", "wjtp")

  def beforeGraphConstruction(): Unit = { }

  def afterGraphConstruction() { }

  def initAllocationSites(): Unit = {
    val listener = Scene.v().getReachableMethods.listener()
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

    val body = method.retrieveActiveBody()
    val blocks = new ExceptionalBlockGraph(body)

    val EP = createNode(method)

    addEntryPointEdgesHCD(method, EP, ListBuffer[ValueNodeType](), blocks, 0, true, ListBuffer[Int]())

  }

  class ValueNodeType(va: Int, stmt: StmtNode) {
    val value: Int = va
    val nodeStmt: StmtNode = stmt

    def show(): String = stmt.stmt.toString

    override def equals(o: Any): Boolean = {
      o match {
        case stmt: StmtNode => stmt.value == value && stmt.stmt == nodeStmt
        case _ => false
      }
    }
  }

  def ValueNodeType(va: Int, stmt: StmtNode) {
    val value: Int = va
    val nodeStmt: StmtNode = stmt


  }

  def addEntryPointEdgesHCD(method: SootMethod, fromNode: StmtNode, visitedBlocks: ListBuffer[ValueNodeType], blocks: ExceptionalBlockGraph, value: Int, typeEdge: Boolean, exitList: ListBuffer[Int]): Unit = {

    visitedBlocks.foreach( x =>{
      if (x.value == value && x.nodeStmt == fromNode) {
        return
      }
    })

    addEPEdgeToBlock(method, fromNode, blocks, value, typeEdge)

    var block = blocks.getBlocks.get(value)

    visitedBlocks += new ValueNodeType(value, fromNode)

    if (block.getSuccs.size() == 2) {

      var ifStmt = blockToIfstatement(blocks, value)
      var stmNodeX = createNode(method, ifStmt).stmt
      visitedBlocks.foreach( x =>{
        if (x.nodeStmt.stmt.stmt == stmNodeX.stmt) {
          return
        }
      })
      var succ = block.getSuccs
      var pred = block.getPreds

      var enterBlock = block.getSuccs.get(0).getIndexInMethod
      var exitBlock = block.getSuccs.get(1).getIndexInMethod
      var aux = exitBlock

      var ll = ListBuffer[Boolean]()
      var ll2 = ListBuffer[Int]()

      hasAPathFromTo(blocks.getBlocks.get(enterBlock), block, ll, ll2)
      var hasAPathLoop = false
      if (ll.contains(true)){
        hasAPathLoop = true
      }

      var hasReturnEdge = false
      pred.forEach(x =>{
        if (x.getIndexInMethod > block.getIndexInMethod){
          hasReturnEdge = true
        }
      })

      var valueHDC = HCD(blocks, value)

      var isAnIf = false

      var l2 = ListBuffer[Boolean]()
      var l3 = ListBuffer[Int]()
      l3 += block.getIndexInMethod
      hasAPathFromTo(block.getSuccs.get(0), block.getSuccs.get(1), l2, l3)
      if (l2.contains(true)){
        isAnIf = true
      }

      var hasReturn = false

      var x = ListBuffer[Boolean]()
      var y = ListBuffer[Int]()
      y += block.getIndexInMethod
      hasAPathFromTo(blocks.getBlocks.get(exitBlock) , block, x, y)
      if (x.contains(true)){
        hasReturn = true
      }

      var w = ListBuffer[Boolean]()
      var z = ListBuffer[Int]()

      z += exitBlock
      var itsDoWhile = true
      hasAPathFromToDoWhile(blocks.getBlocks.get(enterBlock), block, exitBlock, w, z)
      if (w.contains(true)){
        itsDoWhile = false
      }

      var isAnIfElse = false

      if (enterBlock < valueHDC && exitBlock < valueHDC && valueHDC != -1) {
        isAnIfElse = true
      }

      if (valueHDC == -1 || isAnIf) {
        var exitNotVisited = true
        if (isAnIf && !itsDoWhile) {
//          println(value + " It's an IF")
          exitList.foreach(x =>{
            if (x == exitBlock) {
              exitNotVisited = false
            }
          })
          if (hasReturnEdge){
            exitNotVisited = true
          }

          exitList += exitBlock
        }else if (!itsDoWhile){
//          println(value + " It's a WHILE or FOR")
        }
        if (itsDoWhile){ //swap enter and exit blocks
          exitBlock = enterBlock
          enterBlock = aux
//          println(value + " It's a DO-WHILE")
        }

        if (exitBlock > value+1 && exitNotVisited) {
          addEntryPointEdgesHCD(method, fromNode, visitedBlocks, blocks, exitBlock, true, exitList)
        }

        if (itsDoWhile) {
          addEntryPointEdgesHCD(method, fromNode, visitedBlocks, blocks, exitBlock, true, ListBuffer[Int]())
        }

        addEntryPointEdgesHCD(method, createNode(method, ifStmt), visitedBlocks, blocks, enterBlock, false, exitList)

      }else{
//        println(value + " It's an IF-ELSE")

        addEntryPointEdgesHCD(method, fromNode, visitedBlocks, blocks, valueHDC, typeEdge, exitList)

        visitedBlocks += new ValueNodeType(valueHDC, createNode(method, ifStmt))
        exitList += valueHDC

          addEntryPointEdgesHCD(method, createNode(method, ifStmt), visitedBlocks, blocks, exitBlock, false, exitList)
          addEntryPointEdgesHCD(method, createNode(method, ifStmt), visitedBlocks, blocks, enterBlock, true, exitList)

      }

    }else if (block.getSuccs.size() == 1){
      exitList.foreach(x =>{
        if (x == block.getSuccs.get(0).getIndexInMethod) {
          return
        }
      })
      if (isNotContainsGoto(block.getSuccs.get(0)) && isNotContainsGoto(block)) {
        addEntryPointEdgesHCD(method, fromNode, visitedBlocks, blocks, block.getSuccs.get(0).getIndexInMethod, typeEdge, exitList)
      }
    }
  }

  def getBlockContainsIf(blocks: ExceptionalBlockGraph, stm: String): Int = {
    var pos = 0
    for (i <- 0 to (blocks.getBlocks.size()-1)){
      blocks.getBlocks.get(i).forEach(stmBlock =>{
        if (stm.equals(stmBlock.toString())){
          pos = i
        }
      })
    }
    return pos
  }

  def isNotContainsGoto(block: Block): Boolean = {
    var contains = true
    block.forEach(x => {
      if (x.isInstanceOf[GotoStmt]) {
        contains = false
      }
    })
    return contains
  }


  def hasReturnEdge(block: Block, value: Int, visitedPaths: ListBuffer[Boolean]): Unit = {
    val suc = block.getSuccs

    suc.forEach(x => {
      if (x.getIndexInMethod == value) {
        visitedPaths += true
        return
      }
      if (x.getIndexInMethod > value)
        hasReturnEdge(x, value, visitedPaths)
    })

  }

  def hasAPathFromToDoWhile(enter: Block, block: Block, exit: Int, visitedPaths: ListBuffer[Boolean], visitedBlock: ListBuffer[Int]): Unit = {

    if (visitedBlock.contains(enter.getIndexInMethod)) return

    visitedBlock += enter.getIndexInMethod

    val suc = enter.getSuccs

    if (suc.size() > 0){
      suc.forEach(x => {

        if (x.getIndexInMethod == block.getIndexInMethod || x.getIndexInMethod == exit) {
          visitedPaths += true
          return visitedPaths
        }
        if (!visitedBlock.contains(x.getIndexInMethod) && x.getIndexInMethod > block.getIndexInMethod){
          hasAPathFromToDoWhile(x, block, exit, visitedPaths, visitedBlock)
        }

      })
    }
  }

  def hasAPathFromTo(enter: Block, exit: Block, visitedPaths: ListBuffer[Boolean], visitedBlock: ListBuffer[Int]): Unit = {

    if (visitedBlock.contains(enter.getIndexInMethod)) return

    visitedBlock += enter.getIndexInMethod

    val suc = enter.getSuccs

    if (suc.size() > 0){
      suc.forEach(x => {

        if (x.getIndexInMethod == exit.getIndexInMethod) {
          visitedPaths += true
          return visitedPaths
        }
        if (!visitedBlock.contains(x.getIndexInMethod)){
          hasAPathFromTo(x, exit, visitedPaths, visitedBlock)
        }

      })
    }
  }

  def hasAPathFromToWith(enter: Block, block: Block, visitedPaths: ListBuffer[Boolean], visitedBlock: ListBuffer[Int]): Unit = {

    if (visitedBlock.contains(enter.getIndexInMethod)) return

    visitedBlock += enter.getIndexInMethod

    val suc = enter.getSuccs

    if (suc.size()>0){
      suc.forEach(x => {
        if (x.getIndexInMethod == block.getIndexInMethod) {
          visitedPaths += true
          return visitedPaths
        }

        hasAPathFromToWith(x, block, visitedPaths, visitedBlock)

      })
    }
  }

  def blockToIfstatement(blocks: ExceptionalBlockGraph, value: Int): soot.Unit ={
    blocks.getBlocks.get(value).forEach(unit => {
      if (unit.isInstanceOf[soot.jimple.IfStmt]){
        return unit
      }
    })
    return null
  }

  def HCD(blocks: ExceptionalBlockGraph, pos: Int): Int = {
    var left =  ListBuffer[Int]()
    var right = ListBuffer[Int]()

    left += pos
    right += pos
    //Succs of left
    var lIndex =  blocks.getBlocks.get(pos).getSuccs.get(0).getIndexInMethod
    getSuccsDFS(blocks, lIndex, left)

    //Succs of right
    var rIndex = blocks.getBlocks.get(pos).getSuccs.get(1).getIndexInMethod
    getSuccsDFS(blocks, rIndex, right)

    //    TODO: to use Binary Search
    for (l <- left) {
      if (right.contains(l) && l != pos){
        //        println("HCD de "+pos+" : "+l)
        return l
      }
    }
    return -1
  }

  def getSuccsDFS(blocks: ExceptionalBlockGraph, v: Int, visited: ListBuffer[Int]): Unit = {
    if (visited.contains(v)) {
      return
    }else {
      visited += v
      var block = blocks.getBlocks.get(v).getSuccs
      for (i <- 0 to block.size()-1){
        var vActual = block.get(i).getIndexInMethod
        if (!visited.contains(vActual)) {
          getSuccsDFS(blocks, vActual, visited)
        }
      }
    }
  }

  def addEPEdgeToBlock(method: SootMethod, sourceStmt: StmtNode, blocks: ExceptionalBlockGraph, value: Int, typeEdge: Boolean): Unit = {
    blocks.getBlocks.get(value).forEach(unit => {
      if (sourceStmt==null){
        addNodeEP(method, unit)
      }else{
        addCDEdgeFromIf(sourceStmt, unit, method, typeEdge)
      }
    })
  }

  def addEPEdges(method: SootMethod, block: Block): Unit = {
    block.forEach(unit => {
      addNodeEP(method, unit)
    })
  }

  def addNodeEP(method: SootMethod, unit: soot.Unit): Boolean = {
    val source = createNode(method)
    val target = createNode(method, unit)
    addEdgeControlDependency(source, target, true)
  }

  def addCDEdgeFromIf(sourceStmt: StmtNode, targetStmt: soot.Unit, method: SootMethod, typeEdge: Boolean): Unit ={
    //    val source = createNode(method, sourceStmt)
    val target = createNode(method, targetStmt)
    //    if (targetStmt.isInstanceOf[GotoStmt]){
    //      targetStmt.getUnitBoxes.forEach(tStmt => {
    //        if (pos <=3 && pos>0) {
    //          addNodeEP(method, tStmt.getUnit)
    //          addEntryPointEdges(method, z, pos)
    //        }
    //      })
    //    }else { //Remove the else to add Goto Edge
    if (!targetStmt.isInstanceOf[GotoStmt]){
      addEdgeControlDependency(sourceStmt, target, typeEdge)
    }
  }

  def getBlockPositionThatContainsStatement(method: SootMethod, stm: soot.Unit): Int = {
    val body  = method.retrieveActiveBody()
    val blocks = new ExceptionalBlockGraph(body)
    return getBlockPositionThatContainsStatement(blocks, stm)
  }

  def getBlockPositionThatContainsStatement(blocks: ExceptionalBlockGraph, stm: soot.Unit): Int = {
    var pos = 0
    for (i <- 0 to (blocks.getBlocks.size()-1)){
      blocks.getBlocks.get(i).forEach(stmBlock =>{
        if (stm.equals(stmBlock)){
          pos = i
        }
      })
    }
    return pos
  }


  def addEdgeControlDependency(source: LambdaNode, target: LambdaNode, typeEdge: Boolean): Boolean = {
    var res = false
    if(!runInFullSparsenessMode() || true) {
      val label = new StringLabel( if (typeEdge) (ControlDependency.toString) else (ControlDependencyFalse.toString))
      svg.addEdge(source, target, label)
      res = true
    }
    return res
  }



  /*
     * This rule deals with the following situation:
     *
     * (*) p = q
     *
     * In this case, we create an edge from defs(q)
     * to the statement p = q.
     */
  private def copyRule(targetStmt: soot.Unit, local: Local, method: SootMethod, defs: SimpleLocalDefs) = {
    defs.getDefsOfAt(local, targetStmt).forEach(sourceStmt => {
      val source = createNode(method, sourceStmt)
      val target = createNode(method, targetStmt)
      updateGraph(source, target)
    })
  }


  /*
   * creates a graph node from a sootMethod / sootUnit
   */
  def createNode(method: SootMethod, stmt: soot.Unit): StmtNode =
    new StmtNode(Stmt(method.getDeclaringClass.toString, method.getSignature, stmt.toString, stmt.getJavaSourceStartLineNumber), analyze(stmt))

  def createNode(method: SootMethod): StmtNode =
    new StmtNode(Stmt(method.getDeclaringClass.toString, method.getSignature, "Entry Point", 0), SimpleNode)

  def createCSOpenLabel(method: SootMethod, stmt: soot.Unit, sourceStmt: soot.Unit, callee: SootMethod): CallSiteLabel =
    new CallSiteLabel(CallSite(method.getDeclaringClass.toString, method.getSignature,
      stmt.toString, stmt.getJavaSourceStartLineNumber, sourceStmt.toString, callee.toString), CallSiteOpenEdge)

  def createCSCloseLabel(method: SootMethod, stmt: soot.Unit, sourceStmt: soot.Unit, callee: SootMethod): CallSiteLabel =
    new CallSiteLabel(CallSite(method.getDeclaringClass.toString, method.getSignature,
      stmt.toString, stmt.getJavaSourceStartLineNumber, sourceStmt.toString, callee.toString), CallSiteCloseEdge)


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
