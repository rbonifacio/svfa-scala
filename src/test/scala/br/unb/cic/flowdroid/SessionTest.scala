package br.unb.cic.flowdroid

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class SessionTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {

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

class SessionTestSuite extends FunSuite {

  test("description: Session1") {
    val svfa = new SessionTest("securibench.micro.session.Session1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Session2") {
    val svfa = new SessionTest("securibench.micro.session.Session2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Session3") {
    val svfa = new SessionTest("securibench.micro.session.Session3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }
}
