package br.unb.cic.soot.basic

import br.unb.cic.soot.JSVFATest
import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import br.unb.cic.soot.svfa.{CHA, SPARK_LIBRARY}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class Basic11Test extends JSVFATest {
  override def getClassName(): String = "samples.basic.Basic11"
  override def getMainMethod(): String = "main"
}


