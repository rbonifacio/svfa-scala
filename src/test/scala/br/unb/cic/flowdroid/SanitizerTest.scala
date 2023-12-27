package br.unb.cic.flowdroid

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class SanitizerTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {

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

class SanitizerTestSuite extends FunSuite {

  test("description: Sanitizer1") {
    val svfa = new SanitizerTest("securibench.micro.sanitizers.Sanitizers1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Sanitizer2") {
    val svfa = new SanitizerTest("securibench.micro.sanitizers.Sanitizers2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Sanitizer3") {
    val svfa = new SanitizerTest("securibench.micro.sanitizers.Sanitizers3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }

  test("description: Sanitizer4") {
    val svfa = new SanitizerTest("securibench.micro.sanitizers.Sanitizers4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("description: Sanitizer5") {
    val svfa = new SanitizerTest("securibench.micro.sanitizers.Sanitizers5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Sanitizer6") {
    val svfa = new SanitizerTest("securibench.micro.sanitizers.Sanitizers6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }
}
