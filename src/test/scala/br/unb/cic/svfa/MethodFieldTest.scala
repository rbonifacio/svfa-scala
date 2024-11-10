package br.unb.cic.soot
import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}

class MethodFieldTest extends JSVFATest {
  override def getClassName(): String = "samples.MethodFieldSample"

  override def getMainMethod(): String = "m"

  override def analyze(unit: soot.Unit): NodeType =
    unit.getJavaSourceStartLineNumber match {
      case 7 | 10 | 11 | 12 => SourceNode
      case 8 => SinkNode
      case _ => SimpleNode
    }
}
