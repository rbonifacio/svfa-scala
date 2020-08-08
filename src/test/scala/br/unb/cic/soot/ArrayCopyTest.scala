package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import org.scalatest.{BeforeAndAfter, FunSuite}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class ArrayCopyTest extends JSVFATest {
  override def getClassName(): String = "samples.ArrayCopySample"
  override def getMainMethod(): String = "main"
}


