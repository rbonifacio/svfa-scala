package br.unb.cic.soot

import br.unb.cic.soot.aliasing.Aliasing5Test
import br.unb.cic.soot.basic.{Basic11Test, Basic16StringTest, Basic16Test, Basic31Test}
import org.scalatest.{BeforeAndAfter, FunSuite}

class TestCSSuite extends FunSuite with BeforeAndAfter {

  test("[Basic ?] Call Single Method") {
    val svfa = new CallSingleMethodTest()
    svfa.buildSparseValueFlowGraph()
    //    println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("[Basic ?] Call Class Method One") {
    val svfa = new CallClassMethodOnceTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("[Basic ?] Call Class Method Many") {
    val svfa = new CallClassMethodManyTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("[Basic ?] Call Class Method Once Fancy") {
    val svfa = new CallClassMethodOnceFancyTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("[Basic ?] Call Class Method Many Fancy") {
    val svfa = new CallClassMethodManyFancyTest()
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }

  test("[Basic ?] Call Class Method Many Fancy V2") {
    val svfa = new CallClassMethodManyFancyV2Test
    svfa.buildSparseValueFlowGraph()
    println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }
}
