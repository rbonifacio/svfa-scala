package br.ufpe.cin.soot.pdg.rules

import soot.{SootMethod}

abstract class PhantomMethodRule {
  def check(sootMethod: SootMethod) : Boolean
}

class ArrayCopyRule extends PhantomMethodRule {
  override def check(sootMethod: SootMethod): Boolean =
    sootMethod.getDeclaringClass.getName == "java.lang.System" && sootMethod.getName == "arraycopy"
}