package br.unb.cic.soot.svfa.configuration

import java.io.File

import soot._

trait SootConfiguration {

  protected var pointsToAnalysis: PointsToAnalysis = _

  def configurePackages(): List[String] = List("cg", "wjtp")

  def createSceneTransform(): (String, Transform)

  def configureSoot()

  def beforeGraphConstruction(): scala.Unit = {}

  def afterGraphConstruction(): scala.Unit = {}

  def pathToJCE(): String =
    System.getProperty(
      "java.home"
    ) + File.separator + "lib" + File.separator + "jce.jar"

  def pathToRT(): String =
    System.getProperty(
      "java.home"
    ) + File.separator + "lib" + File.separator + "rt.jar"

}
