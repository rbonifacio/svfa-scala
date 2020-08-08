package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import org.scalatest.{BeforeAndAfter, FunSuite}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class InitStringFromCharArrayTest extends JSVFATest {
  override def getClassName(): String = "samples.InitStringFromCharArraySample"
  override def getMainMethod(): String = "main"
}
