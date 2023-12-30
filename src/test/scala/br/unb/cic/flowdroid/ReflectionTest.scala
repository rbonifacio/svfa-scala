package br.unb.cic.flowdroid

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class ReflectionTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {

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

class ReflectionTestSuite extends FunSuite {

//  test("description: Reflection1") {
//    val svfa = new ReflectionTest("securibench.micro.reflection.Refl1", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
//
//  test("description: Reflection2") {
//    val svfa = new ReflectionTest("securibench.micro.reflection.Refl2", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
//
//  test("description: Reflection3") {
//    val svfa = new ReflectionTest("securibench.micro.reflection.Refl3", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
//
//  test("description: Reflection4") {
//    val svfa = new ReflectionTest("securibench.micro.reflection.Refl4", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 1)
//  }
}
