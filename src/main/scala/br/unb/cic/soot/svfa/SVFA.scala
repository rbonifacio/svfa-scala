package br.unb.cic.soot.svfa

import java.io.File
import br.unb.cic.soot.graph.{CallSiteLabel, CallSiteOpenLabel, FieldSensitiveLabel, FieldSensitiveStoreLabel, GraphNode, SinkNode, SourceNode, StatementNode, StringLabel}
import soot._
import soot.options.Options

import scala.collection.JavaConverters._
import scala.collection.mutable


sealed trait CG

case object CHA extends CG

case object SPARK_LIBRARY extends CG

case object SPARK extends CG

/**
 * Base class for all implementations
 * of SVFA algorithms.
 */
abstract class SVFA {

  protected var pointsToAnalysis: PointsToAnalysis = _
  var svg = new br.unb.cic.soot.graph.Graph()

  def sootClassPath(): String

  def applicationClassPath(): List[String]

  def getEntryPoints(): List[SootMethod]

  def getIncludeList(): List[String]

  def createSceneTransform(): (String, Transform)

  def configurePackages(): List[String]

  def beforeGraphConstruction()

  def afterGraphConstruction()

  def callGraph(): CG = SPARK

  def buildSparseValueFlowGraph() {
    configureSoot()
    beforeGraphConstruction()
    val (pack, t) = createSceneTransform()
    PackManager.v().getPack(pack).add(t)
    configurePackages().foreach(p => PackManager.v().getPack(p).apply())
    afterGraphConstruction()
  }

  def configureSoot() {
    G.reset()
    Options.v().set_no_bodies_for_excluded(true)
    Options.v().set_allow_phantom_refs(true)
    Options.v().set_include(getIncludeList().asJava);
    Options.v().set_output_format(Options.output_format_none)
    Options.v().set_whole_program(true)
    Options.v().set_soot_classpath(sootClassPath() + File.pathSeparator + pathToJCE() + File.pathSeparator + pathToRT())
    Options.v().set_process_dir(applicationClassPath().asJava)
    Options.v().set_full_resolver(true)
    Options.v().set_keep_line_number(true)
    Options.v().set_prepend_classpath(true)

    configureCallGraphPhase()

    Scene.v().loadNecessaryClasses()
    Scene.v().setEntryPoints(getEntryPoints().asJava)
  }

  def configureCallGraphPhase() {
    callGraph() match {
      case CHA => Options.v().setPhaseOption("cg.cha", "on")
      case SPARK => Options.v().setPhaseOption("cg.spark", "on")
      case SPARK_LIBRARY => {
        Options.v().setPhaseOption("cg.spark", "on")
        Options.v().setPhaseOption("cg", "library:any-subtype")
      }
    }
  }

  def findConflictingPaths(): scala.collection.Set[List[GraphNode]] = {
    if (svg.fullGraph) {
      val conflicts = svg.findPathsFullGraph()
      return conflicts.toSet
    } else {
      val sourceNodes = svg.nodes.filter(n => n.nodeType == SourceNode)
      val sinkNodes = svg.nodes.filter(n => n.nodeType == SinkNode)

      //      val conflicts = for(source <- sourceNodes; sink <- sinkNodes)
      //         yield svg.findPath(source, sink)

      var conflicts: List[List[GraphNode]] = List()
      sourceNodes.foreach(source => {
        sinkNodes.foreach(sink => {
          val paths = svg.findPath(source, sink)
          conflicts = conflicts ++ paths
        })
      })

      conflicts.filter(p => p.nonEmpty).toSet
    }
  }


  def reportConflicts(): scala.collection.Set[String] =
    findConflictingPaths().map(p => p.toString)

  def pathToJCE(): String =
    System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar"

  def pathToRT(): String =
    System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"

  def svgToDotModel(): String = {
    val s = new StringBuilder
    var nodeColor = ""
    s ++= "digraph { \n"

    for (n <- svg.nodes) {
      nodeColor = n.nodeType match {
        case SourceNode => "[fillcolor=blue, style=filled]"
        case SinkNode => "[fillcolor=red, style=filled]"
        case _ => ""
      }

      s ++= " " + "\"" + n.show() + "\"" + " " + nodeColor + "\n"
    }
    s  ++= "\n"

//    for (n <- svg.nodes) {
//      val adjacencyList = svg.getAdjacentNodes(n).get
//      val edges = adjacencyList.map(next => "\"" + n.show() + "\"" + " -> " + "\"" + next.show() + "\"")
//      for (e <- edges) {
//        s ++= " " + e + "\n"
//      }
//    }


    for (e <- svg.edges) {
      val edge = "\"" + e.from.show() + "\"" + " -> " + "\"" + e.to.show() + "\""

      val label: String = e.label match {
        case c: CallSiteLabel =>  {
          if (c.labelType == CallSiteOpenLabel) { "[label=\"cs(\"]" }
          else { "[label=\"cs)\"]" }
        }
        case f: FieldSensitiveLabel => {
          if (f.labelType == FieldSensitiveStoreLabel) { "[label=\"fsStore\"]" }
          else { "[label=\"fsLoad\"]" }
        }
        case _ => ""
      }

      s ++= " " + edge + " " + label + "\n"
    }

    s ++= "}"

    return s.toString()
  }


}
