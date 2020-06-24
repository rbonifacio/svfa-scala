package br.unb.cic.soot.svfa

import java.io.File

import br.unb.cic.soot.graph.{Graph, Node, SinkNode, SourceNode}
import soot._
import soot.options.Options

import scala.collection.JavaConverters._

/**
  * Base class for all implementations
  * of SVFA algorithms.
  */
abstract class SVFA {

   protected var pointsToAnalysis : PointsToAnalysis = _
   var svg: Graph[Node] = new br.unb.cic.soot.graph.Graph()

   def sootClassPath(): String
   def applicationClassPath(): List[String]
   def getEntryPoints(): List[SootMethod]
   def getIncludeList(): List[String]
   def createSceneTransform(): (String, Transform)
   def configurePackages(): List[String]
   def beforeGraphConstruction()
   def afterGraphConstruction()

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
      Options.v().set_whole_program(true)
      Options.v().setPhaseOption("cg.spark", "on")
      Options.v().setPhaseOption("cg", "library:any-subtype")
      Options.v().set_output_format(Options.output_format_none)
      Options.v().set_soot_classpath(sootClassPath() + File.pathSeparator + pathToJCE() + File.pathSeparator + pathToRT())
      Options.v().set_process_dir(applicationClassPath().asJava)
      Options.v().set_include(getIncludeList().asJava);
      Options.v().set_full_resolver(true)
      Options.v().set_no_bodies_for_excluded(true)
      Options.v().set_allow_phantom_refs(true)
      Options.v().set_keep_line_number(true)
      Options.v().set_prepend_classpath(true)
      Scene.v().loadNecessaryClasses()
      Scene.v().setEntryPoints(getEntryPoints().asJava)
   }

   def reportConflicts(): scala.collection.Set[String] = {
      val sourceNodes = svg.nodes.filter((n: Node) => n.nodeType == SourceNode)
      val sinkNodes = svg.nodes.filter((n: Node) => n.nodeType == SinkNode)

      val conflicts = for(source <- sourceNodes; sink <- sinkNodes)
                      yield svg.findPath(source, sink)

      return conflicts.filter(p => None != p)
                      .map(p => p.toString)
   }

   def pathToJCE():String =
      System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar"

   def pathToRT():String =
      System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"

   def svgToDotModel(): String = {
      val s = new StringBuilder
      var nodeColor = ""
      s ++= "digraph { \n"

      for(n <- svg.nodes()) {
         nodeColor = n.nodeType match  {
            case SourceNode => "[fillcolor=blue, style=filled]"
            case SinkNode   => "[fillcolor=red, style=filled]"
            case _          => ""
         }

         s ++= " " + "\"" + n.stmt + "\"" + nodeColor + "\n"
      }

      for(n <- svg.nodes) {
         val adjacencyList = svg.map.get(n).get
         val edges = adjacencyList.map(next => "\"" + n.stmt + "\"" + " -> " + "\"" + next.stmt + "\"")
         for(e <- edges) {
            s ++= " " + e + "\n"
         }
      }

      s ++= "}"

      return s.toString()
   }
}