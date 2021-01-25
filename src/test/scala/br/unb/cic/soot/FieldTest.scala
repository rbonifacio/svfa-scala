package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}

class FieldTest extends JSVFATest {
  override def getClassName(): String = "samples.FieldSample"

  override def getMainMethod(): String = "m"

  override def analyze(unit: soot.Unit): NodeType = {
    if (unit.getJavaSourceStartLineNumber == 12) {
      return SourceNode
    }
    if (unit.getJavaSourceStartLineNumber == 13 || unit.getJavaSourceStartLineNumber == 17) {
      return SinkNode
    }
    return SimpleNode
  }
}
