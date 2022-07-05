package br.unb.cic.soot

import br.unb.cic.soot.caseStudy.{CSSample1Test, CSSample2Test, SimpleNoTaintTest, SimpleTaintTest, SimpleObjectTaintTest}
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestSuiteCaseStudy extends FunSuite with BeforeAndAfter {

  test("I am trying to learn context sensitivity") {
    val svfa = new CSSample1Test()
    svfa.buildSparseValueFlowGraph()
    //println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  test("I am trying to learn context sensitivity II") {
    val svfa = new CSSample2Test()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 2)
  }

  test("I am a simple test of TAINT") {
    val svfa = new SimpleTaintTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    assert(svfa.reportConflicts().size == 1)
  }

  test("I am a simple test of NO TAINT") {
    val svfa = new SimpleNoTaintTest()
    svfa.buildSparseValueFlowGraph()
    assert(svfa.reportConflicts().size == 0)
  }

  test("I am a simple test of Object TAINT") {
    val svfa = new SimpleObjectTaintTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    //assert(svfa.reportConflicts().size == 1)
  }

}
