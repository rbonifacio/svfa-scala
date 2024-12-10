package br.unb.cic.soot.svfa.jimple.rules

import soot.SootMethod
import soot.jimple.Stmt
import soot.toolkits.scalar.SimpleLocalDefs

/** The common interface for every rule action. It defines a functional
  * interface with a method apply that takes a soot method, an statement (with
  * an invoke expression), and a list of local definitions.
  */
trait RuleAction extends ((SootMethod, Stmt, SimpleLocalDefs) => Unit)

/** A list composition of rule actions.
  */
trait ComposedRuleAction extends RuleAction {
  def actions: List[RuleAction]

  override def apply(
      sootMethod: SootMethod,
      stmt: Stmt,
      localDefs: SimpleLocalDefs
  ): Unit = {
    actions.foreach(action => action.apply(sootMethod, stmt, localDefs))
  }
}

/** The root class in the hierarchy of method rules. Every method rule must be
  * composed with a RuleAction. In this way, we have two orthogonal hierarchies:
  * one that expresses the constraint for triggering an action and another that
  * defines an action.
  */
abstract class MethodRule extends RuleAction {
  def check(sootMethod: SootMethod): Boolean

  def run(
      sootMethod: SootMethod,
      invokeStmt: Stmt,
      localDefs: SimpleLocalDefs
  ): Unit = {
    if (check(invokeStmt.getInvokeExpr.getMethod)) {
      apply(sootMethod, invokeStmt, localDefs)
    }
  }
}

/** Rule for named method.
  * @param className
  *   the class name of the method
  * @param methodName
  *   the method name
  */
abstract class NamedMethodRule(className: String, methodName: String)
    extends MethodRule {
  override def check(sootMethod: SootMethod): Boolean =
    sootMethod.getDeclaringClass.getName == className && sootMethod.getName == methodName
}

/** Rule for native methods.
  */
abstract class NativeRule() extends MethodRule {
  override def check(sootMethod: SootMethod): Boolean = sootMethod.isNative
}

/** Rule for methods without active body
  */
abstract class MissingActiveBodyRule() extends MethodRule {
  override def check(sootMethod: SootMethod): Boolean =
    sootMethod.isPhantom || (!sootMethod.hasActiveBody && sootMethod.getSource == null)
}

/** An action that does not execute anything
  */
trait DoNothing extends RuleAction {
  def apply(
      sootMethod: SootMethod,
      invokeStmt: Stmt,
      localDefs: SimpleLocalDefs
  ) = {}
}
