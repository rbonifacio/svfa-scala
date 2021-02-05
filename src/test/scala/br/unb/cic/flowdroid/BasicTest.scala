package br.unb.cic.flowdroid

import br.unb.cic.soot.graph.{NodeType, _}
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class BasicTest(var className: String = "", var mainMethod: String = "") extends FlowdroidSpec {
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
    if (sourceList.contains(exp.getMethod.getSignature)) {
      return SourceNode;
    } else if (sinkList.contains(exp.getMethod.getSignature)) {
      return SinkNode;
    }
    return SimpleNode;
  }
}

class BasicTestSuite extends FunSuite {
  test("in the class Basic2 we should detect 1 conflict of a simple XSS test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic0", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic1 we should detect 1 conflict of a simple XSS test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic1", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic2 we should detect 1 conflict of a XSS combined with a simple conditional test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic2", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic3 we should detect 1 conflict of a simple derived string test, very similar to Basic0") {
    val svfa = new BasicTest("securibench.micro.basic.Basic3", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic4 we should detect 1 conflict of a sensitive path test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic4", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic5 we should detect 3 conflicts of a moderately complex derived string test") {
    val svfa = new BasicTest("securibench.micro.basic.Basic5", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 3)
  }

  ignore("in the class Basic6 we should detect 1 conflict of a complex derived string test") {
    val svfa = new BasicTest("securibench.micro.basic.Basic6", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic7 we should detect 1 conflict of a complex derived string with buffers test") {
    val svfa = new BasicTest("securibench.micro.basic.Basic7", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic8 we should detect 1 conflict of a complex conditional test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic8", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic9 we should detect 1 conflict of a chain of assignments test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic9", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic10 we should detect 1 conflict of a chain of assignments and buffers test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic10", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic11 we should detect 2 conflicts of a simple derived string test with a false positive") {
    val svfa = new BasicTest("securibench.micro.basic.Basic11", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  test("in the class Basic12 we should detect 2 conflicts of a simple conditional test case where both sides have sinks") {
    val svfa = new BasicTest("securibench.micro.basic.Basic12", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  test("in the class Basic13 we should detect 1 conflict of a simple test case, the source method was modified to getInitParameterInstead") {
    val svfa = new BasicTest("securibench.micro.basic.Basic13", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic14 we should detect 1 conflict of a servlet context and casts test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic14", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic15 we should detect 1 conflict of a casts more exhaustively test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic15", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic16 we should detect 1 conflict of a store statement in heap-allocated data structures test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic16", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic17 we should detect 1 conflict of a store statement in heap-allocated data structures and a false positive test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic17", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic18 we should detect 1 conflict of a simple loop unrolling test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic18", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic19 we should detect 1 conflict of a simple SQL injection with prepared statements test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic19", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic20 we should detect 1 conflict of a simple SQL injection test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic20", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic21 we should detect 4 conflicts in a SQL injection with less commonly used methods test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic21", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 4)
  }

  ignore("in the class Basic22 we should detect 1 conflict in a basic path traversal test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic22", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic23 we should detect 3 conflicts in a path traversal test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic23", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 3)
  }

  ignore("in the class Basic24 we should detect 1 conflict in a unsafe redirect test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic24", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic25 we should detect 1 conflict in a test getParameterValues test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic25", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic26 we should detect 1 conflict in a getParameterMap test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic26", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic27 we should detect 1 conflict in a getParameterMap test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic27", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic28 we should detect 2 conflicts in a complicated control flow test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic28", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  ignore("in the class Basic29 we should detect 2 conflicts in a recursive data structures test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic29", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  ignore("in the class Basic30 we should detect 1 conflict in a field sensitivity test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic30", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic31 we should detect 2 conflicts in a values obtained from cookies test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic31", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  test("in the class Basic32 we should detect 1 conflict in a values obtained from headers test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic32", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic33 we should detect 1 conflict in a values obtained from headers test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic33", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic34 we should detect 2 conflicts in a values obtained from headers test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic34", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  test("in the class Basic35 we should detect 6 conflicts in a values obtained from HttpServletRequest test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic35", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 6)
  }

  ignore("in the class Basic36 we should detect 1 conflict in a values obtained from HttpServletRequest input stream test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic36", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic37 we should detect 1 conflict in a StringTokenizer test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic37", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic38 we should detect 1 conflict in a StringTokenizer with a false positive test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic38", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic39 we should detect 1 conflict in a StringTokenizer test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic39", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("in the class Basic41 we should detect 1 conflict in a use getInitParameter instead test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic41", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("in the class Basic42 we should detect 1 conflict in a use getInitParameterNames test case") {
    val svfa = new BasicTest("securibench.micro.basic.Basic42", "doGet")
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }
}
