package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import org.scalatest.{BeforeAndAfter, FunSuite}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class ArrayTest extends JSVFATest {
  override def getClassName(): String = "samples.ArraySample"
  override def getMainMethod(): String = "main"
}