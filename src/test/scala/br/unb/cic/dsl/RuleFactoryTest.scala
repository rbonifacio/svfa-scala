package br.unb.cic.dsl

import br.unb.cic.soot.JSVFATest
import br.unb.cic.soot.graph.{NodeType, SimpleNode}
import br.unb.cic.soot.svfa.jimple.dsl.RuleFactory
import br.unb.cic.soot.svfa.jimple.rules.{MethodRule, NativeRule}
import org.scalatest.FunSuite

class RuleFactoryTest {
  val rule = new RuleFactory(new JSVFATest {
    override def getClassName(): String = ""

    override def getMainMethod(): String = ""

    override def analyze(unit: soot.Unit): NodeType = SimpleNode
  })

  def apply(rule: String, actions: List[String]): MethodRule = {
    this.rule.create(rule, actions)
  }
}

class RuleFactotyTestSuite extends FunSuite {
  test("NativeRule with DoNothing") {
    var rule = new RuleFactoryTest().apply("NativeRule", List("DoNothing"))
    assert(rule.isInstanceOf[NativeRule])
  }
}
