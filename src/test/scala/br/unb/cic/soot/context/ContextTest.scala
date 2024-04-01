package br.unb.cic.soot.context

import br.unb.cic.soot.JSVFATest
import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class ContextTest(var className: String = "", var mainMethod: String = "") extends JSVFATest {

  override def getClassName(): String = className

  override def getMainMethod(): String = mainMethod

  override def analyze(unit: soot.Unit): NodeType = {
    if(unit.isInstanceOf[InvokeStmt]) {
      val invokeStmt = unit.asInstanceOf[InvokeStmt]
      return analyzeInvokeStmt(invokeStmt.getInvokeExpr)
    }
    if(unit.isInstanceOf[soot.jimple.AssignStmt]) {
      val assignStmt = unit.asInstanceOf[AssignStmt]
      if(assignStmt.getRightOp.isInstanceOf[InvokeExpr]) {
        val invokeStmt = assignStmt.getRightOp.asInstanceOf[InvokeExpr]
        return analyzeInvokeStmt(invokeStmt)
      }
    }
    return SimpleNode
  }

  def analyzeInvokeStmt(exp: InvokeExpr) : NodeType =
    exp.getMethod.getName match {
      case "source" => SourceNode
      case "sink"   => SinkNode
      case _        => SimpleNode
    }
}

class ContextTestSuite extends FunSuite {

  test("C1") {
    val svfa = new ContextTest("samples.context.Context1", "main")
    svfa.buildSparseValueFlowGraph()
//    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("C2") {
    val svfa = new ContextTest("samples.context.Context2", "main")
    svfa.buildSparseValueFlowGraph()
//    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("C3") {
    val svfa = new ContextTest("samples.context.Context3", "main")
    svfa.buildSparseValueFlowGraph()
//    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("C3_PRE") {
    val svfa = new ContextTest("samples.context.Context3Pre", "main")
    svfa.buildSparseValueFlowGraph()
    //    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("C3_POST") {
    val svfa = new ContextTest("samples.context.Context3Post", "main")
    svfa.buildSparseValueFlowGraph()
    //    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("C4") {
    val svfa = new ContextTest("samples.context.Context4", "main")
    svfa.buildSparseValueFlowGraph()
//    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("C41") {
    val svfa = new ContextTest("samples.context.Context41", "main")
    svfa.buildSparseValueFlowGraph()
    //    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("C42") {
    val svfa = new ContextTest("samples.context.Context42", "main")
    svfa.buildSparseValueFlowGraph()
    //    print(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }
}


