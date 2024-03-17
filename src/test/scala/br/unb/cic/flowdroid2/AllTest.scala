package br.unb.cic.flowdroid2

import br.unb.cic.soot.graph._
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class AllTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {
  override def getClassName(): String = className

  override def getMainMethod(): String = mainMethod

  override def analyze(unit: soot.Unit): NodeType = {
    if (unit.isInstanceOf[InvokeStmt]) {
      val invokeStmt = unit.asInstanceOf[InvokeStmt]
      return analyzeInvokeExpr(invokeStmt.getInvokeExpr)
    }
    if (unit.isInstanceOf[soot.jimple.AssignStmt]) {
      val assignStmt = unit.asInstanceOf[AssignStmt]
      if (assignStmt.getRightOp.isInstanceOf[InvokeExpr]) {
        val invokeExpr = assignStmt.getRightOp.asInstanceOf[InvokeExpr]
        return analyzeInvokeExpr(invokeExpr)
      }
    }
    SimpleNode
  }

  def analyzeInvokeExpr(exp: InvokeExpr): NodeType = {
    if (sourceList.contains(exp.getMethod.getSignature)) {
      return SourceNode
    } else if (sinkList.contains(exp.getMethod.getSignature)) {
      return SinkNode
    }
    SimpleNode
  }
}

class AllTestSuite extends FunSuite {

  /**
   * ALIASING TESTs
   */

  test("in the class Aliasing1 we should detect 1 conflict of a simple aliasing test case") {
    val svfa = new AllTest("securibench.micro.aliasing.Aliasing1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Aliasing2 we should not detect any conflict in this false positive test case") {
    val svfa = new AllTest("securibench.micro.aliasing.Aliasing2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().isEmpty)
 }

  test("in the class Aliasing3 we should not detect any conflict, but in Flowdroid this test case was not conclusive") {
    val svfa = new AllTest("securibench.micro.aliasing.Aliasing3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().isEmpty)
  }

  test("in the class Aliasing4 we should detect 2 conflict") {
    val svfa = new AllTest("securibench.micro.aliasing.Aliasing4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("in the class Aliasing5 we should detect 1 conflict") {
    val svfa = new AllTest("securibench.micro.aliasing.Aliasing5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Aliasing6 we should detect 7 conflicts") {
    val svfa = new AllTest("securibench.micro.aliasing.Aliasing6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 7)
  }

//  test("in the class Aliasing7 we should detect 7 conflicts") {
//    val svfa = new AllTest("securibench.micro.aliasing.Aliasing7", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 7)
//  }
//
//  test("in the class Aliasing8 we should detect 8 conflicts") {
//    val svfa = new AllTest("securibench.micro.aliasing.Aliasing8", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 8)
//  }
//
//  test("in the class Aliasing9 we should detect 1 conflicts") {
//    val svfa = new AllTest("securibench.micro.aliasing.Aliasing9", "doGet")
//    svfa.buildSparseValueFlowGraph()
//    assert(svfa.reportConflictsSVG().size == 2)
//  }

  /**
   * ARRAY TESTs
   */

  test("description: Array1") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array2") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array3") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array4") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array5") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().isEmpty)
  }

  test("description: Array6") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array7") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays7", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array8") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays8", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array9") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays9", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Array10") {
    val svfa = new AllTest("securibench.micro.arrays.Arrays10", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  /**
   * BASIC TESTs
   */

  test("in the class Basic2 we should detect 1 conflict of a simple XSS test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic0", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic1 we should detect 1 conflict of a simple XSS test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic2 we should detect 1 conflict of a XSS combined with a simple conditional test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic3 we should detect 1 conflict of a simple derived string test, very similar to Basic0") {
    val svfa = new AllTest("securibench.micro.basic.Basic3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic4 we should detect 1 conflict of a sensitive path test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic5 we should detect 3 conflicts of a moderately complex derived string test") {
    val svfa = new AllTest("securibench.micro.basic.Basic5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 3)
  }

  //TODO: it looks like a flaky test.
  test("in the class Basic6 we should detect 1 conflict of a complex derived string test") {
    val svfa = new AllTest("securibench.micro.basic.Basic6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic7 we should detect 1 conflict of a complex derived string with buffers test") {
    val svfa = new AllTest("securibench.micro.basic.Basic7", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic8 we should detect 1 conflict of a complex conditional test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic8", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic9 we should detect 1 conflict of a chain of assignments test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic9", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic10 we should detect 1 conflict of a chain of assignments and buffers test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic10", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic11 we should detect 2 conflicts of a simple derived string test with a false positive") {
    val svfa = new AllTest("securibench.micro.basic.Basic11", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("in the class Basic12 we should detect 2 conflicts of a simple conditional test case where both sides have sinks") {
    val svfa = new AllTest("securibench.micro.basic.Basic12", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("in the class Basic13 we should detect 1 conflict of a simple test case, the source method was modified to getInitParameterInstead") {
    val svfa = new AllTest("securibench.micro.basic.Basic13", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic14 we should detect 1 conflict of a servlet context and casts test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic14", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic15 we should detect 1 conflict of a casts more exhaustively test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic15", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic16 we should detect 1 conflict of a store statement in heap-allocated data structures test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic16", "doGet")
    svfa.buildSparseValueFlowGraph()
    // println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic17 we should detect 1 conflict of a store statement in heap-allocated data structures and a false positive test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic17", "doGet")
    svfa.buildSparseValueFlowGraph()
    // println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1) // the search should be context sensitive
  }

  test("in the class Basic18 we should detect 1 conflict of a simple loop unrolling test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic18", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic19 we should detect 1 conflict of a simple SQL injection with prepared statements test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic19", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic20 we should detect 1 conflict of a simple SQL injection test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic20", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic21 we should detect 4 conflicts in a SQL injection with less commonly used methods test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic21", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 4)
  }

  test("in the class Basic22 we should detect 1 conflict in a basic path traversal test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic22", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic23 we should detect 3 conflicts in a path traversal test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic23", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 3)
  }

  test("in the class Basic24 we should detect 1 conflict in a unsafe redirect test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic24", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic25 we should detect 1 conflict in a test getParameterValues test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic25", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic26 we should detect 1 conflict in a getParameterMap test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic26", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic27 we should detect 1 conflict in a getParameterMap test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic27", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic28 we should detect 2 conflicts in a complicated control flow test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic28", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic29 we should detect 2 conflicts in a recursive data structures test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic29", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("in the class Basic30 we should detect 1 conflict in a field sensitivity test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic30", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic31 we should detect 3 conflicts in a values obtained from cookies test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic31", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 3)
  }

  test("in the class Basic32 we should detect 1 conflict in a values obtained from headers test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic32", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic33 we should detect 1 conflict in a values obtained from headers test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic33", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic34 we should detect 2 conflicts in a values obtained from headers test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic34", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("in the class Basic35 we should detect 6 conflicts in a values obtained from HttpServletRequest test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic35", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 6)
  }

  test("in the class Basic36 we should detect 1 conflict in a values obtained from HttpServletRequest input stream test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic36", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic37 we should detect 1 conflict in a StringTokenizer test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic37", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic38 we should detect 1 conflict in a StringTokenizer with a false positive test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic38", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic39 we should detect 1 conflict in a StringTokenizer test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic39", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic41 we should detect 1 conflict in a use getInitParameter instead test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic41", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("in the class Basic42 we should detect 1 conflict in a use getInitParameterNames test case") {
    val svfa = new AllTest("securibench.micro.basic.Basic42", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  /**
   * COLLECTION TESTs
   */

  test("description: Collection1") {
    val svfa = new AllTest("securibench.micro.collections.Collections1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection2") {
    val svfa = new AllTest("securibench.micro.collections.Collections2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection3") {
    val svfa = new AllTest("securibench.micro.collections.Collections3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("description: Collection4") {
    val svfa = new AllTest("securibench.micro.collections.Collections4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection5") {
    val svfa = new AllTest("securibench.micro.collections.Collections5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection6") {
    val svfa = new AllTest("securibench.micro.collections.Collections6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection7") {
    val svfa = new AllTest("securibench.micro.collections.Collections7", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection8") {
    val svfa = new AllTest("securibench.micro.collections.Collections8", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection9") {
    val svfa = new AllTest("securibench.micro.collections.Collections9", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection10") {
    val svfa = new AllTest("securibench.micro.collections.Collections10", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection11") {
    val svfa = new AllTest("securibench.micro.collections.Collections11", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection11b") {
    val svfa = new AllTest("securibench.micro.collections.Collections11b", "foo")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection12") {
    val svfa = new AllTest("securibench.micro.collections.Collections12", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection13") {
    val svfa = new AllTest("securibench.micro.collections.Collections13", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Collection14") {
    val svfa = new AllTest("securibench.micro.collections.Collections14", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  /**
   * DATASTRUCTURE TESTs
   */

  test("description: DataStructure1") {
    val svfa = new AllTest("securibench.micro.datastructures.Datastructures1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: DataStructure2") {
    val svfa = new AllTest("securibench.micro.datastructures.Datastructures2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: DataStructure3") {
    val svfa = new AllTest("securibench.micro.datastructures.Datastructures3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: DataStructure4") {
    val svfa = new AllTest("securibench.micro.datastructures.Datastructures4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: DataStructure5") {
    val svfa = new AllTest("securibench.micro.datastructures.Datastructures5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: DataStructure6") {
    val svfa = new AllTest("securibench.micro.datastructures.Datastructures6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  /**
   * FACTORY TESTs
   */

  test("description: Factory1") {
    val svfa = new AllTest("securibench.micro.factories.Factories1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Factory2") {
    val svfa = new AllTest("securibench.micro.factories.Factories2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Factory3") {
    val svfa = new AllTest("securibench.micro.factories.Factories3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  /**
   * INTER TESTs
   */
  
  test("description: Inter1") {
    val svfa = new AllTest("securibench.micro.inter.Inter1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter2") {
    val svfa = new AllTest("securibench.micro.inter.Inter2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("description: Inter3") {
    val svfa = new AllTest("securibench.micro.inter.Inter3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter4") {
    val svfa = new AllTest("securibench.micro.inter.Inter4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("description: Inter5") {
    val svfa = new AllTest("securibench.micro.inter.Inter5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 2)
  }

  test("description: Inter6") {
    val svfa = new AllTest("securibench.micro.inter.Inter6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter7") {
    val svfa = new AllTest("securibench.micro.inter.Inter7", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter8") {
    val svfa = new AllTest("securibench.micro.inter.Inter8", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter9") {
    val svfa = new AllTest("securibench.micro.inter.Inter9", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter10") {
    val svfa = new AllTest("securibench.micro.inter.Inter10", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter11") {
    val svfa = new AllTest("securibench.micro.inter.Inter11", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Inter12") {
    val svfa = new AllTest("securibench.micro.inter.Inter12", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }


  test("description: Inter13") {
    val svfa = new AllTest("securibench.micro.inter.Inter13", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }


  test("description: Inter14") {
    val svfa = new AllTest("securibench.micro.inter.Inter14", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  /**
   * SESSION TESTs
   */

  test("description: Session1") {
    val svfa = new AllTest("securibench.micro.session.Session1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Session2") {
    val svfa = new AllTest("securibench.micro.session.Session2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: Session3") {
    val svfa = new AllTest("securibench.micro.session.Session3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  /**
   * STRONG UPDATE TESTs
   */

  test("description: StrongUpdate1") {
    val svfa = new AllTest("securibench.micro.strong_updates.StrongUpdates1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }

  test("description: StrongUpdate2") {
    val svfa = new AllTest("securibench.micro.strong_updates.StrongUpdates2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }

  test("description: StrongUpdate3") {
    val svfa = new AllTest("securibench.micro.strong_updates.StrongUpdates3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }

  test("description: StrongUpdate4") {
    val svfa = new AllTest("securibench.micro.strong_updates.StrongUpdates4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("description: StrongUpdate5") {
    val svfa = new AllTest("securibench.micro.strong_updates.StrongUpdates5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflictsSVG().size == 0)
  }
}
