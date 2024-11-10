package br.unb.cic.soot.svfa

import java.io.File
import soot._
import soot.options.Options
trait SootConfiguration {

  protected var pointsToAnalysis: PointsToAnalysis = _

  def sootClassPath(): String

  def applicationClassPath(): List[String]

  def configurePackages(): List[String]

  def getEntryPoints(): List[SootMethod]

  def getIncludeList(): List[String]

  def createSceneTransform(): (String, Transform)
  def configureSoot()

  def beforeGraphConstruction(): scala.Unit = {}

  def afterGraphConstruction(): scala.Unit = {}

  def pathToJCE(): String =
    System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar"

  def pathToRT(): String =
    System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"


}