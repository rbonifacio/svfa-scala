package br.unb.cic.soot

import br.unb.cic.soot.basic.{
  Basic11Test, Basic16StringTest, Basic16Test, Basic31Test, CSSample1Test, SimpleTaintTest, SimpleNoTaintTest
}
import org.scalatest.{BeforeAndAfter, FunSuite, Ignore}
import samples.FieldSample
import samples.basic.Basic31

class TestSuite extends FunSuite with BeforeAndAfter {

  ignore("we should find exactly three conflicts in this analysis") {
    val svfa = new ArrayTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 3)
  }

  test("we should correctly compute the number of nodes and edges in the BlackBoardTest sample") {
    val svfa = new BlackBoardTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.nodes.size == 10)
    assert(svfa.svg.numberOfEdges() == 12)
  }

  test("we should not find any conflict in the BlackBoardTest sample") {
    val svfa = new BlackBoardTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 0)
  }

  ignore("we should correctly compute the number of nodes and edges of the CC16Test sample") {
    val svfa = new CC16Test()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.nodes.size == 13)
    assert(svfa.svg.numberOfEdges() == 14)
  }

  test("we should find exactly one conflict of the CC16Test sample") {
    val svfa = new CC16Test()
    svfa.buildSparseValueFlowGraph()
    // println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("we should correctly compute the number of nodes and edges of the IfElseTest sample") {
    val svfa = new IfElseTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.svg.nodes.size == 17)
  }

  ignore("we should correctly compute the number of edges of the IfElseTest sample") {
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
    assert(svfa.reportConflicts().size == 1)
  }

  test("we should find exactly one conflict in the InitStringBufferTest analysis") {
    val svfa = new InitStringBufferTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  ignore("we should find exactly one conflict in the StringConcatTest analysis") {
    val svfa = new StringConcatTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 6)
  }

//  test("we should find exactly one conflict in the StringGetCharsTest analysis") {
//    val svfa = new StringGetCharsTest()
//    svfa.buildSparseValueFlowGraph()
//    println(svfa.svgToDotModel())
//    assert(svfa.reportConflicts().size == 1)
//  }

  test("we should find exactly one conflict in the StringToStringTest analysis") {
    val svfa = new StringToStringTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("we should find exactly two conflicts in the basic.Basic11 analysis") {
    val svfa = new Basic11Test()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  test("we should find exactly one conflicts in the basic.Basic16 analysis") {
    val svfa = new Basic16Test()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("we should find exactly one conflicts in the basic.Basic31 analysis") {
    val svfa = new Basic31Test()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 2)
  }

  test("we should find exactly one conflicts in the basic.Basic16String analysis") {
    val svfa = new Basic16StringTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 1)
  }

  test("we should find exactly one conflict in the ContextSensitiveSample  analysis") {
    val svfa = new ContextSensitiveTest()
    svfa.buildSparseValueFlowGraph()
    // println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  test("we should find exactly one conflict in the FieldSample analysis") {
    val svfa = new FieldTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)   // NOTE: We are not traversing the body of
                                               //       a method associated to a SinkNode. 
  }

  // This is the case with fields that the source method
  // changes the field that is subsequently used by a sink line
  ignore("we should find exactly one conflict in the MethodFieldTest analysis") {
    val svfa = new MethodFieldTest()
    svfa.buildSparseValueFlowGraph()
    System.out.println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size >= 1)
  }

  // This is a simple case that the with a local variable would be detected
  // but with the field variable it isn't detected
  ignore("we should find exactly one conflict in the InvokeInstanceMethodOnFieldTest analysis") {
    val svfa = new InvokeInstanceMethodOnFieldTest()
    svfa.buildSparseValueFlowGraph()
    System.out.println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size >= 1)
  }

  // This case is representative of the problem with abstract classes and interfaces
  // that causes the analysis to not be able to infer the concrete implementation of the
  // methods.
  ignore("we should find exactly one conflict in the HashmapTest analysis") {
    val svfa = new HashmapTest()
    svfa.buildSparseValueFlowGraph()
    System.out.println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size >= 1)
  }

  test("I am trying to learn context sensitivity") {
    val svfa = new CSSample1Test()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    //println(svfa.reportConflicts().size)
    //assert(svfa.reportConflicts().size == 2)
  }

  test("I am a simple test of TAINT") {
    val svfa = new SimpleTaintTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    //println(svfa.reportConflicts().size)
    //assert(svfa.reportConflicts().size == 2)
  }

  test("I am a simple test of NO TAINT") {
    val svfa = new SimpleNoTaintTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    //println(svfa.reportConflicts().size)
    //assert(svfa.reportConflicts().size == 2)
  }

}
