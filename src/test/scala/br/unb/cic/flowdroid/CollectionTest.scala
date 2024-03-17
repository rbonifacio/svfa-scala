package br.unb.cic.flowdroid

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class CollectionTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {

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

class CollectionSuite extends FunSuite {

  test("description: Collection1") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection2") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection3") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  ignore("description: Collection4") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection5") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection6") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection7") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections7", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection8") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections8", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection9") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections9", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection10") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections10", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection11") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections11", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection11b") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections11b", "foo")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection12") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections12", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection13") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections13", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  ignore("description: Collection14") {
    val svfa = new CollectionTest("securibench.micro.collections.Collections14", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }
}
