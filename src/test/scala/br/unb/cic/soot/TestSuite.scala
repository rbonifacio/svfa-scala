package br.unb.cic.soot

import org.scalatest.{BeforeAndAfter, FunSuite}

class TestSuite extends FunSuite with BeforeAndAfter {

  test("we should find exactly three conflicts in this analysis") {
    val svfa = new ArrayTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 3)
  }

  test("we should correctly compute the number of nodes and edges in the BlackBoardTest sample") {
    val svfa = new BlackBoardTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.nodes.size == 10)
    assert(svfa.svg.numberOfEdges() == 11)
  }

  test("we should not find any conflict in the BlackBoardTest sample") {
    val svfa = new BlackBoardTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 0)
  }

  test("we should correctly compute the number of nodes and edges of the CC16Test sample") {
    val svfa = new CC16Test()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.nodes.size == 13)
    assert(svfa.svg.numberOfEdges() == 14)
  }

  test("we should find exactly one conflict of the CC16Test sample") {
    val svfa = new CC16Test()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("we should correctly compute the number of nodes and edges of the IfElseTest sample") {
    val svfa = new IfElseTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.nodes.size == 17)
  }

  test("we should correctly compute the number of edges of the IfElseTest sample") {
    val svfa = new IfElseTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.numberOfEdges() == 18)
  }

  test("we should find exactly one conflict in this analysis of the IfElseTest sample") {
    val svfa = new IfElseTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("we should find two conflicts in the LogbackSampleTest analysis") {
    val svfa = new LogbackSampleTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 3)
  }

  test("we should find exactly one conflict in the StringBuggerTest analysis") {
    val svfa = new StringBufferTest()
    svfa.buildSparseValueFlowGraph()
    System.out.println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

//  test("we should find exactly one conflict in the StringGetCharsTest analysis") {
//    val svfa = new StringGetCharsTest()
//    svfa.buildSparseValueFlowGraph()
//    System.out.println(svfa.svgToDotModel())
//    assert(svfa.reportConflicts().size == 1)
//  }

  ignore("we should find exactly one conflict in the StringToStringTest analysis") {
    val svfa = new StringToStringTest()
    svfa.buildSparseValueFlowGraph()
    System.out.println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 2)
  }

}
