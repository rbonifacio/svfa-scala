package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, _}
import org.scalatest.{BeforeAndAfter, FunSuite}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class FieldSampleTest(var className: String = "", var mainMethod: String = "") extends JSVFATest {
  override def getClassName(): String = className

  override def getMainMethod(): String = mainMethod

  override def runInFullSparsenessMode() = false

  override def analyze(unit: soot.Unit): NodeType = {
    unit match {
      case invokeStmt: InvokeStmt =>
        return analyzeInvokeStmt(invokeStmt.getInvokeExpr)
      case assignStmt: AssignStmt =>
        assignStmt.getRightOp match {
          case invokeStmt: InvokeExpr =>
            return analyzeInvokeStmt(invokeStmt)
          case _ =>
            return SimpleNode
        }
      case _ => return SimpleNode
    }
  }

  def analyzeInvokeStmt(exp: InvokeExpr): NodeType = {
    exp.getMethod.getName match {
      case "source" => SourceNode
      case "sink" => SinkNode
      case _ => SimpleNode
    }
  }
}


class FieldSamplesTestSuite extends FunSuite with BeforeAndAfter {
  ignore("in the class FieldSample01 we should detect 1 conflict in a direct sink of a tainted field") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample01", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  // LIMITATION: Field overwrite is not supported
  ignore("in the class FieldSample02 we should not detect any conflict because the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample02", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 0)
  }

  ignore("in the class FieldSample03 we should detect 1 conflict in a direct sink of a tainted field of a " +
    "contained object") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample03", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  // In the field sensitive analysis we not mark the whole object as tainted,
  // in this case the sink of the object is not detected as a conflict
  ignore("in the class FieldSample04 we should not detect any conflict because the contained tainted object " +
    "and the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample04", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class FieldSample05 we should detect 1 conflict in a direct sink of a object with a tainted field") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample05", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  // LIMITATION: The Spark not support field sensitive analysis with context sensitivity
  ignore("in the class FieldSample06 we should not detect any conflict because the contained tainted object " +
    "and the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample06", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class FieldSample07 we should detect 1 conflict in a direct sink of a object with a tainted field") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample07", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  // LIMITATION: Field overwrite is not supported
  ignore("in the class FieldSample08 we should not detect any conflict because the contained tainted object " +
    "and the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample08", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 0)
  }

  ignore("in the class FieldSample09 we should not detect any conflict because the contained tainted object " +
    "and the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample09", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  // LIMITATION: Primitive type variables (like Int and String) created without 'new'
  // are not detected by the Spark
  ignore("in the class FieldSample10 we should detect 1 conflict in a direct sink of a tainted field") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample10", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class FieldSample11 we should not detect any conflict because the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample11", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 0)
  }

  ignore("in the class FieldSample12 we should detect 1 conflict in a direct sink of a tainted field of a " +
    "contained object") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample12", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  // LIMITATION: Field overwrite is not supported
  ignore("in the class FieldSample13 we should not detect any conflict because the contained tainted object " +
    "and the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample13", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 0)
  }

  // LIMITATION: Primitive type variables (like Int and String) created without 'new'
  // are not detected by the Spark
  ignore("in the class FieldSample14 we should detect 1 conflict in a direct sink of a object with a tainted field") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample14", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  // LIMITATION: The Spark not support field sensitive analysis with context sensitivity
  ignore("in the class FieldSample15 we should not detect any conflict because the contained tainted object " +
    "and the tainted field was override") {
    val svfa = new FieldSampleTest("samples.fields.FieldSample15", "main")
    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }
}
