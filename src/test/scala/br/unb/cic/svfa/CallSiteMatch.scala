package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class CallSiteMatch(var className: String = "", var mainMethod: String = "") extends JSVFATest {
  override def getClassName(): String = className

  override def getMainMethod(): String = mainMethod

  override def analyze(unit: soot.Unit): NodeType = {
    if (unit.isInstanceOf[InvokeStmt]) {
      val invokeStmt = unit.asInstanceOf[InvokeStmt]
      return analyzeInvokeExpr(invokeStmt.getInvokeExpr)
    }
    if (unit.isInstanceOf[soot.jimple.AssignStmt]) {
      val assignStmt = unit.asInstanceOf[AssignStmt]
      if (assignStmt.getRightOp.isInstanceOf[InvokeExpr]) {
        val invokeExpr = assignStmt.getRightOp.asInstanceOf[InvokeExpr]
        return analyzeInvokeExpr(invokeExpr)
      }
    }
    SimpleNode
  }

  def analyzeInvokeExpr(exp: InvokeExpr) : NodeType =
    exp.getMethod.getName match {
      case "source" => SourceNode
      case "sink"   => SinkNode
      case _        => SimpleNode
    }
}

class CallSiteMatchTestSuite extends FunSuite {
  test("in the class CallSiteMatch1 we should detect 1 conflict of a unopened callsite test case") {
    val svfa = new CallSiteMatch("samples.callSiteMatch.CallSiteMatch1", "main")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class CallSiteMatch2 we should detect 1 conflict of a unclosed callsite test case") {
    val svfa = new CallSiteMatch("samples.callSiteMatch.CallSiteMatch2", "main")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class CallSiteMatch3 we should detect 1 conflict of a unclosed and unopened " +
    "callsite test case") {
    val svfa = new CallSiteMatch("samples.callSiteMatch.CallSiteMatch3", "main")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("in the class CallSiteMatch4 we should detect 2 conflict of a unclosed and unopened " +
    "callsite with a common method in between test case") {
    val svfa = new CallSiteMatch("samples.callSiteMatch.CallSiteMatch4", "main")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("in the class CallSiteMatch5 we should detect 2 conflict of a balanced callsite test case") {
    val svfa = new CallSiteMatch("samples.callSiteMatch.CallSiteMatch5", "main")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }
}
