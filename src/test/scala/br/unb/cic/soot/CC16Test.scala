package br.unb.cic.soot

import org.scalatest.{BeforeAndAfter, FunSuite}
import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class CC16Test extends JSVFATest {
  override def getClassName(): String = "samples.CC16"
  override def getMainMethod(): String = "main"
}


