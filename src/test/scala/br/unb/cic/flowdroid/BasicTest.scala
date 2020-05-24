package br.unb.cic.flowdroid

import br.unb.cic.soot.graph.{NodeType, _}
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class BasicTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {
  override def getClassName(): String = className

  override def getMainMethod(): String = mainMethod

  override def analyze(unit: soot.Unit): NodeType = {
    if (unit.isInstanceOf[InvokeStmt]) {
      val invokeStmt = unit.asInstanceOf[InvokeStmt]
      return analyzeInvokeStmt(invokeStmt.getInvokeExpr)
    }
    if (unit.isInstanceOf[soot.jimple.AssignStmt]) {
      val assignStmt = unit.asInstanceOf[AssignStmt]
      if (assignStmt.getRightOp.isInstanceOf[InvokeExpr]) {
        val invokeStmt = assignStmt.getRightOp.asInstanceOf[InvokeExpr]
        return analyzeInvokeStmt(invokeStmt)
      }
    }
    return SimpleNode
  }

  def analyzeInvokeStmt(exp: InvokeExpr): NodeType = {
    if (sourceList.contains(exp.getMethod.getSignature)) {
      return SourceNode;
    } else if (sinkList.contains(exp.getMethod.getSignature)) {
      return SinkNode;
    }
    return SimpleNode;
  }
}

class BasicTestSuite extends FunSuite {
  test("in the class Basic0 we should detect 1 conflict of a simple XSS test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic0", "doGet")
    svfa.buildSparseValueFlowGraph()


    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

//    assert(svfa.svg.nodes.size == 4)
//    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic1 we should detect 1 conflict of a simple XSS test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic1", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    assert(svfa.svg.nodes.size == 3)
    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic2 we should detect 1 conflict of a XSS combined with a simple conditional test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic2", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

//    assert(svfa.svg.nodes.size == 4)
//    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic3 we should detect 1 conflict of a simple derived string test, very similar to Basic0") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic3", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic4 we should detect 1 conflict of a sensitive path test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic4", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic5 we should detect 3 conflicts of a moderately complex derived string test") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic5", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 3)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic6 we should detect 1 conflict of a complex derived string test") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic6", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic7 we should detect 1 conflict of a complex derived string with buffers test") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic7", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic8 we should detect 1 conflict of a complex conditional test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic8", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

//    assert(svfa.svg.nodes.size == 4)
    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic9 we should detect 1 conflict of a chain of assignments test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic9", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic10 we should detect 1 conflict of a chain of assignments and buffers test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic10", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic11 we should detect 2 conflicts of a simple derived string test with a false positive") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic11", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 2)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic12 we should detect 2 conflicts of a simple conditional test case where both sides have sinks") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic12", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 4)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 2)
  }

  test("in the class Basic13 we should detect 1 conflict of a simple test case, the source method was modified to getInitParameterInstead") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic13", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    assert(svfa.svg.nodes.size == 3)
    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic14 we should detect 1 conflict of a servlet context and casts test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic14", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

//    assert(svfa.svg.nodes.size == 3)
//    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic15 we should detect 1 conflict of a casts more exhaustively test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic15", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 3)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic16 we should detect 1 conflict of a store statement in heap-allocated data structures test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic16", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 3)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic17 we should detect 1 conflict of a store statement in heap-allocated data structures and a false positive test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic17", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 3)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic18 we should detect 1 conflict of a simple loop unrolling test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic18", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 3)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic19 we should detect 1 conflict of a simple SQL injection with prepared statements test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic19", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 3)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }

  // TODO: this test case is failing at this moment
  test("in the class Basic20 we should detect 1 conflict of a simple SQL injection test case") {
    val svfa = new AliasingTest("securibench.micro.basic.Basic20", "doGet")
    svfa.buildSparseValueFlowGraph()

    // Uncomment the 2 next lines to see the jimple code of method and the nodes and edges of the svfa graph
    //    svfa.jimpleOfMethod()
    //    println(svfa.svgToDotModel())

    //    assert(svfa.svg.nodes.size == 3)
    //    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 1)
  }
}