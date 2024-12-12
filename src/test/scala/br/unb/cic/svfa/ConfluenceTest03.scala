package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class ConfluenceTest03 extends JSVFATest {
  override def getClassName(): String = "samples.fields.Confluence03"

  override def getMainMethod(): String = "main"

  override def analyze(unit: soot.Unit): NodeType = unit.getJavaSourceStartLineNumber match {
    case 26 => SourceNode
    case 14 => SinkNode
    case _  => SimpleNode
  }


}
