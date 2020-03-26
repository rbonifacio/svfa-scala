package br.unb.cic.soot

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import scalax.collection.Graph
import scalax.collection.GraphEdge.DiEdge
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class BlackBoard extends JSVFATest {
  override def getClassName(): String = "samples.BlackBoard"
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

class BlackBoardTestSuite extends FunSuite {

  test("we should correctly compute the number of nodes and edges") {
    val svfa = new BlackBoard()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.nodes.size == 7)
    assert(svfa.svg.numberOfEdges() == 5)
  }

}
