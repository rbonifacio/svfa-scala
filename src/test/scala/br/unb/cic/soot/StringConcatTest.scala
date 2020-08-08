package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class StringConcatTest extends JSVFATest {
  override def getClassName(): String = "samples.StringConcatSample"
  override def getMainMethod(): String = "main"
}


