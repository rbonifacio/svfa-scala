package br.unb.cic.flowdroid

import br.unb.cic.soot.graph.NodeType
import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt, JimpleBody}

class AliasingTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {
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

class AliasingTestSuite extends FunSuite {
  test("in the class Aliasing1 we should detect 1 conflict of a simple aliasing test case") {
    val svfa = new AliasingTest("securibench.micro.aliasing.Aliasing1", "doGet")
    svfa.buildSparseValueFlowGraph()

    assert(svfa.svg.nodes.size == 4)
    assert(svfa.svg.numberOfEdges() == 3)
    assert(svfa.reportConflicts().size == 1)

    svfa.jimpleOfMethod()
    println(svfa.svgToDotModel())
  }

  test("in the class Aliasing2 we should not detect any conflict in this false positive test case") {
    val svfa = new AliasingTest("securibench.micro.aliasing.Aliasing2", "doGet")
    svfa.buildSparseValueFlowGraph()

    assert(svfa.svg.nodes.size == 4)
    assert(svfa.svg.numberOfEdges() == 2)
    assert(svfa.reportConflicts().size == 0)

    svfa.jimpleOfMethod()
    println(svfa.svgToDotModel())

    // maybe the copy rule of sparse value flow analysis is not working correctly
  }

  test("in the class Aliasing3 we should not detect any conflict, but in Flowdroid this test case was not conclusive") {
    val svfa = new AliasingTest("securibench.micro.aliasing.Aliasing3", "doGet")
    svfa.buildSparseValueFlowGraph()

    assert(svfa.svg.nodes.size == 5)
    assert(svfa.svg.numberOfEdges() == 3)
    assert(svfa.reportConflicts().size == 0)

    svfa.jimpleOfMethod()
    println(svfa.svgToDotModel())
  }

  // TODO: this test case is failing at this moment
  test("in the class Aliasing4 we should detect 2 conflict") {
    val svfa = new AliasingTest("securibench.micro.aliasing.Aliasing4", "doGet")
    svfa.buildSparseValueFlowGraph()

    assert(svfa.reportConflicts().size == 2)

    svfa.jimpleOfMethod()
    println(svfa.svgToDotModel())
  }

  // TODO: this test case is failing at this moment
  test("in the class Aliasing5 we should detect 1 conflict") {
    val svfa = new AliasingTest("securibench.micro.aliasing.Aliasing5", "doGet")
    svfa.buildSparseValueFlowGraph()

    assert(svfa.reportConflicts().size == 1)

    svfa.jimpleOfMethod()
    println(svfa.svgToDotModel())
  }

  // TODO: this test case is failing at this moment
  test("in the class Aliasing6 we should detect 7 conflict") {
    val svfa = new AliasingTest("securibench.micro.aliasing.Aliasing6", "doGet")
    svfa.buildSparseValueFlowGraph()

    assert(svfa.reportConflicts().size == 7)

    svfa.jimpleOfMethod()
    println(svfa.svgToDotModel())
  }

}