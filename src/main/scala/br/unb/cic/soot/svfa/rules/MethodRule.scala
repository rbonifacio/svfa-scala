package br.unb.cic.soot.svfa.rules

import br.unb.cic.soot.svfa.SVFA
import soot.{Local, SootMethod}
import soot.jimple.InvokeExpr
import soot.toolkits.scalar.SimpleLocalDefs

abstract class PhantomMethodRule {
  def check(sootMethod: SootMethod) : Boolean
}

class ArrayCopyRule extends PhantomMethodRule {
  override def check(sootMethod: SootMethod): Boolean =
    sootMethod.getDeclaringClass.getName == "java.lang.System" && sootMethod.getName == "arraycopy"
}