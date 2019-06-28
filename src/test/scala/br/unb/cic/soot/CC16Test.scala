package br.unb.cic.soot

import org.scalatest.FunSuite
import br.unb.cic.soot.graph.{Node, NodeType, SimpleNode, SinkNode, SourceNode}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}
import scalax.collection.Graph
import scalax.collection.GraphEdge.DiEdge

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
    return SimpleNode()
  }

  def analyzeInvokeStmt(exp: InvokeExpr) : NodeType =
    exp.getMethod.getName match {
      case "source" => SourceNode()
      case "sink"   => SinkNode()
      case _        => SimpleNode()
    }

  def toDot() : String = {
    val g : Graph[Node, DiEdge] = svg

    val s = new StringBuilder

    s ++= "digraph { \n"
    g.nodes.foreach[Unit](n => {
      s ++= " " + n.stmt + "\n"
    })

    g.edges.foreach[Unit](e => {
      s ++= " " + e.nodes.map(n => n.stmt).mkString(" => ") + " \n"
    })


    s ++= "}"

    println(s)


    return ""
  }
}

class TestSuite extends FunSuite {

  test("we should init soot without throwing any exception") {
    val svfa = new CC16Test()
    svfa.buildSparseValueFlowGraph()
    assert(1 == svfa.methods)
    svfa.toDot()
  }

}
