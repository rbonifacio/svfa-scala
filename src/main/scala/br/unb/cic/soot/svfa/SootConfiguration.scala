package br.unb.cic.soot.svfa

import java.io.File
import soot._
import soot.options.Options
import scala.collection.JavaConverters._

sealed trait CG

case object CHA extends CG
case object SPARK_LIBRARY extends CG
case object SPARK extends CG

/**
 * Base class for all implementations
 * of SVFA algorithms.
 */
abstract class SootConfiguration {

  protected var pointsToAnalysis: PointsToAnalysis = _

  def sootClassPath(): String

  def applicationClassPath(): List[String]

  def getEntryPoints(): List[SootMethod]

  def getIncludeList(): List[String]

  def createSceneTransform(): (String, Transform)

  def configurePackages(): List[String] = List("cg", "wjtp")

  def beforeGraphConstruction(): scala.Unit = {}

  def afterGraphConstruction(): scala.Unit = {}

  def callGraph(): CG = SPARK

  def configureSoot() {
    G.reset()
    Options.v().set_no_bodies_for_excluded(true)
    Options.v().set_allow_phantom_refs(true)
    Options.v().set_include(getIncludeList().asJava);
    Options.v().set_output_format(Options.output_format_none)

    if (getJavaVersion < 9) {
      Options.v.set_prepend_classpath(true)
      Options.v().set_soot_classpath(sootClassPath() + File.pathSeparator + pathToJCE() + File.pathSeparator + pathToRT())
    }
    else if (getJavaVersion >= 9) {
      Options.v.set_soot_classpath(sootClassPath())
    }

    Options.v().set_whole_program(true)
    Options.v().set_process_dir(applicationClassPath().asJava)
    Options.v().set_full_resolver(true)
    Options.v().set_keep_line_number(true)
    Options.v().setPhaseOption("jb", "use-original-names:true")
    Options.v().set_ignore_resolution_errors(true);
    configureCallGraphPhase()

    Scene.v().loadNecessaryClasses()
    Scene.v().setEntryPoints(getEntryPoints().asJava)
  }

  def configureCallGraphPhase() {
    callGraph() match {
      case CHA => Options.v().setPhaseOption("cg.cha", "on")
      case SPARK => {
        Options.v().setPhaseOption("cg.spark", "on")
        Options.v().setPhaseOption("cg.spark", "cs-demand:true")
        Options.v().setPhaseOption("cg.spark", "string-constants:true")
      }
      case SPARK_LIBRARY => {
        Options.v().setPhaseOption("cg.spark", "on")
        Options.v().setPhaseOption("cg", "library:any-subtype")
      }
    }
  }

  def pathToJCE(): String =
    System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar"

  def pathToRT(): String =
    System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"

  private def getJavaVersion = {
    var version = System.getProperty("java.version")
    if (version.startsWith("1.")) version = version.substring(2, 3)
    else {
      val dot = version.indexOf(".")
      if (dot != -1) version = version.substring(0, dot)
    }
    version.toInt
  }
}
