package br.unb.cic.flowdroid

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class ArrayTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {

  override def getClassName(): String = className

  override def getMainMethod(): String = mainMethod

  override def analyze(unit: soot.Unit): NodeType = {
    unit match {
      case invokeStmt: InvokeStmt =>
        analyzeInvokeStmt(invokeStmt.getInvokeExpr)
      case assignStmt: AssignStmt =>
        assignStmt.getRightOp match {
          case invokeStmt: InvokeExpr =>
            analyzeInvokeStmt(invokeStmt)
          case _ =>
            SimpleNode
        }
      case _ => SimpleNode
    }
  }

  def analyzeInvokeStmt(exp: InvokeExpr): NodeType = {
    if (sourceList.contains(exp.getMethod.getSignature)) {
      return SourceNode
    } else if (sinkList.contains(exp.getMethod.getSignature)) {
      return SinkNode
    }
    SimpleNode
  }
}

class ArrayTestSuite extends FunSuite {

  ignore("description: Array1") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Array2") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Array3") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Array4") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array5") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().isEmpty)
  }

  ignore("description: Array6") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Array7") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays7", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Array8") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays8", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Array9") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays9", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Array10") {
    val svfa = new ArrayTest("securibench.micro.arrays.Arrays10", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }
}
