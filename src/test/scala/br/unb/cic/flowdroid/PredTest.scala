package br.unb.cic.flowdroid

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class PredTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {

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

class PredTestSuite extends FunSuite {

//  test("description: Pred1") {
//    val svfa = new PredTest("securibench.micro.pred.Pred1", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 0)
//  }
//
//  test("description: Pred2") {
//    val svfa = new PredTest("securibench.micro.pred.Pred2", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
//
//  test("description: Pred3") {
//    val svfa = new PredTest("securibench.micro.pred.Pred3", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 0)
//  }
//
//  test("description: Pred4") {
//    val svfa = new PredTest("securibench.micro.pred.Pred4", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
//
//  test("description: Pred5") {
//    val svfa = new PredTest("securibench.micro.pred.Pred5", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
//
//  test("description: Pred6") {
//    val svfa = new PredTest("securibench.micro.pred.Pred6", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 0)
//  }
//
//  test("description: Pred7") {
//    val svfa = new PredTest("securibench.micro.pred.Pred7", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 0)
//  }
//
//  test("description: Pred8") {
//    val svfa = new PredTest("securibench.micro.pred.Pred8", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
//
//  test("description: Pred9") {
//    val svfa = new PredTest("securibench.micro.pred.Pred9", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
}
