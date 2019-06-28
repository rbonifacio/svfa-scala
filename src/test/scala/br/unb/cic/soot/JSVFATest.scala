package br.unb.cic.soot

import br.unb.cic.soot.svfa.JSVFA
import soot.{Scene, SootMethod}

abstract class JSVFATest extends JSVFA {
  def getClassName(): String
  def getMainMethod(): String

  override def sootClassPath(): String = ""

  override def applicationClassPath(): List[String] = List("target/scala-2.12/test-classes")

  override def getEntryPoints(): List[SootMethod] = {
    val sootClass = Scene.v().getSootClass(getClassName())
    val sootMethod = sootClass.getMethodByName(getMainMethod())
    return List(sootMethod)
  }
}
