package br.unb.cic.soot

import br.unb.cic.soot.graph._
import org.scalatest.{BeforeAndAfter, FunSuite}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class LogbackSampleTest extends JSVFATest {
  override def getClassName(): String = "samples.LogbackSample"

  override def getMainMethod(): String = "main"

  // In this case, we use the source code line number
  // to state which statements are source or sink.
  override def analyze(unit: soot.Unit): NodeType =
    unit.getJavaSourceStartLineNumber match {
      case 21 => SourceNode
      case 29 | 30 => SinkNode
      case _ => SimpleNode
    }
}

class LogbackSampleTestSuite extends FunSuite with BeforeAndAfter {

  test("we should find two conflicts in this analysis") {
    val svfa = new LogbackSampleTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }
}