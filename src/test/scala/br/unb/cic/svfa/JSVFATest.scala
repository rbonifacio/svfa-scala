package br.unb.cic.soot

import br.unb.cic.soot.svfa.configuration.JavaSootConfiguration
import br.unb.cic.soot.svfa.jimple.{FieldSensitive, Interprocedural, JSVFA, PropagateTaint}
import soot.{Scene, SootMethod}

abstract class JSVFATest extends JSVFA with JavaSootConfiguration with Interprocedural with FieldSensitive with PropagateTaint {
  def getClassName(): String
  def getMainMethod(): String

  override def sootClassPath(): String = ""
  
  override def applicationClassPath(): List[String] = List("target/scala-2.12/test-classes", System.getProperty("user.home")+"/.m2/repository/javax/servlet/javax.servlet-api/3.0.1/javax.servlet-api-3.0.1.jar")

  override def getEntryPoints(): List[SootMethod] = {
    val sootClass = Scene.v().getSootClass(getClassName())
    List(sootClass.getMethodByName(getMainMethod()))
  }

  override def getIncludeList(): List[String] = List(
      "java.lang.*",
      "java.util.*"
    )
}
