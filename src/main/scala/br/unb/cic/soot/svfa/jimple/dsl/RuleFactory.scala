package br.unb.cic.soot.svfa.jimple.dsl

import br.unb.cic.soot.svfa.jimple.JSVFA
import br.unb.cic.soot.svfa.jimple.rules._

import scala.collection.mutable.HashMap

class RuleFactory(val jsvfa: JSVFA) {
  def create(
      rule: String,
      actions: List[String],
      params: HashMap[String, String] = HashMap.empty[String, String],
      definitions: HashMap[String, HashMap[String, Int]] =
        HashMap.empty[String, HashMap[String, Int]]
  ): MethodRule = {

    var ruleActions = List.empty[RuleAction]
    actions.foreach(action => {
      action match {
        case "DoNothing" =>
          ruleActions = ruleActions ++ List(new DoNothing {})
        case "CopyBetweenArgs" =>
          ruleActions = ruleActions ++ List(new jsvfa.CopyBetweenArgs {
            override def from: Int = definitions(action)("from")

            override def target: Int = definitions(action)("target")
          })
        case "CopyFromMethodArgumentToBaseObject" =>
          ruleActions =
            ruleActions ++ List(new jsvfa.CopyFromMethodArgumentToBaseObject {
              override def from: Int = definitions(action)("from")
            })
        case "CopyFromMethodArgumentToLocal" =>
          ruleActions =
            ruleActions ++ List(new jsvfa.CopyFromMethodArgumentToLocal {
              override def from: Int = definitions(action)("from")
            })
        case "CopyFromMethodCallToLocal" =>
          ruleActions =
            ruleActions ++ List(new jsvfa.CopyFromMethodCallToLocal {})
        case _ =>
          ruleActions = ruleActions ++ List(new DoNothing {})
      }
    })

    rule match {
      case "NativeRule" => {
        new NativeRule with ComposedRuleAction {
          override def actions: List[RuleAction] = ruleActions
        }
      }
      case "MissingActiveBodyRule" => {
        new MissingActiveBodyRule with ComposedRuleAction {
          override def actions: List[RuleAction] = ruleActions
        }
      }
      case "NamedMethodRule" => {
        new NamedMethodRule(params("className"), params("methodName"))
          with ComposedRuleAction {
          override def actions: List[RuleAction] = ruleActions
        }
      }
    }

  }
}
