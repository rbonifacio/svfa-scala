package br.ufpe.cin.soot

import br.ufpe.cin.soot.pdg.jimple.{FieldSenstive, Interprocedural, JPDG}
import soot.{Scene, SootMethod}

abstract class JPDGTest extends JPDG with Interprocedural with FieldSenstive {
  def getClassName(): String
  def getMainMethod(): String

  override def sootClassPath(): String = ""
  
  override def applicationClassPath(): List[String] = List("target/scala-2.12/test-classes", System.getProperty("user.home")+"/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar")

  override def getEntryPoints(): List[SootMethod] = {
    val sootClass = Scene.v().getSootClass(getClassName())
    List(sootClass.getMethodByName(getMainMethod()))
  }

  override def getIncludeList(): List[String] = List(
      "java.lang.*",
      "java.util.*"
    )
}
