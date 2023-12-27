package br.unb.cic.flowdroid

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class StrongUpdateTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {

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

class StrongUpdateTestSuite extends FunSuite {

  test("description: StrongUpdate1") {
    val svfa = new StrongUpdateTest("securibench.micro.strong_updates.StrongUpdates1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }

  test("description: StrongUpdate2") {
    val svfa = new StrongUpdateTest("securibench.micro.strong_updates.StrongUpdates2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }

  test("description: StrongUpdate3") {
    val svfa = new StrongUpdateTest("securibench.micro.strong_updates.StrongUpdates3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }

  test("description: StrongUpdate4") {
    val svfa = new StrongUpdateTest("securibench.micro.strong_updates.StrongUpdates4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: StrongUpdate5") {
    val svfa = new StrongUpdateTest("securibench.micro.strong_updates.StrongUpdates5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }
}
