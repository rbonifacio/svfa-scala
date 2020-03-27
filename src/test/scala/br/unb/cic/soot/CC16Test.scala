package br.unb.cic.soot

import org.scalatest.{BeforeAndAfter, FunSuite}
import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class CC16Test extends JSVFATest {
  override def getClassName(): String = "samples.CC16"
  override def getMainMethod(): String = "main"

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

class CC16TestSuite extends FunSuite with BeforeAndAfter {
  val svfa = new CC16Test()
  before {
    svfa.buildSparseValueFlowGraph()
  }

  test("we should correctly compute the number of nodes and edges") {
    assert(svfa.svg.nodes.size == 10)
    assert(svfa.svg.numberOfEdges() == 10)
  }

  test("we should find exactly one conflict in this analysis") {
    assert(svfa.reportConflicts().size == 1)
  }

}
