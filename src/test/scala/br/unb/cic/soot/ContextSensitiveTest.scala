package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class ContextSensitiveTest extends JSVFATest {
  override def getClassName(): String = "samples.ContextSensitiveSample"
  override def getMainMethod(): String = "main"
}