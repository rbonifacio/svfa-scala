package br.unb.cic.soot.svfa

import java.io.File

import br.unb.cic.soot.graph.LambdaNode

// TODO: uncomment this line if the new implementation not work
//import br.unb.cic.soot.graph.{Graph, Node, SinkNode, SourceNode}
// TODO: remove these lines if the new implementation not work
// import br.unb.cic.soot.graph.{Node, SinkNode, SourceNode, LambdaGraph}
import br.unb.cic.soot.graph.{Node, SinkNode, SourceNode, Graph}

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
abstract class SVFA {

   protected var pointsToAnalysis : PointsToAnalysis = _
   // TODO: uncomment the code below if the new implementations do not work
//   var svg: Graph[Node] = new br.unb.cic.soot.graph.Graph()
   // TODO: remove the code below if the new implementations do not work
//   var svg: lambdaGraph = new br.unb.cic.soot.graph.lambdaGraph()
   var svg = new br.unb.cic.soot.graph.Graph()

   def sootClassPath(): String
   def applicationClassPath(): List[String]
   def getEntryPoints(): List[SootMethod]
   def getIncludeList(): List[String]
   def createSceneTransform(): (String, Transform)
   def configurePackages(): List[String]
   def beforeGraphConstruction()
   def afterGraphConstruction()
   def callGraph() : CG = SPARK

   def buildSparseValueFlowGraph() {
      configureSoot()
      beforeGraphConstruction()
      val (pack, t) = createSceneTransform()
      PackManager.v().getPack(pack).add(t)
      configurePackages().foreach(p =>  PackManager.v().getPack(p).apply())
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
   // TODO: uncomment the code below if the new implementations do not work
   def reportConflicts(): scala.collection.Set[String] = {
// TODO: remove the code below if the new implementations do not work
//   def reportConflicts(): List[String] = {
      // TODO: uncomment the code below if the new implementations do not work
//      val sourceNodes = svg.nodes.filter((n: Node) => n.nodeType == SourceNode)
//      val sinkNodes = svg.nodes.filter((n: Node) => n.nodeType == SinkNode)
// TODO: remove the code below if the new implementations do not work
//      val sourceNodes = svg.getNodes().filter(n => n.nodeType == SourceNode)
//      val sinkNodes = svg.getNodes().filter(n => n.nodeType == SinkNode)
      val sourceNodes = svg.nodes.filter(n => n.nodeType == SourceNode)
      val sinkNodes = svg.nodes.filter(n => n.nodeType == SinkNode)

      val conflicts = for(source <- sourceNodes; sink <- sinkNodes)
                      yield svg.findPath(source, sink)
      // TODO: uncomment the code below if the new implementations do not work
      conflicts.filter(p => None != p).map(p => p.toString)
      // TODO: remove the code below if the new implementations do not work
//      var newConflicts: List[List[LambdaNode]] = List()
//      conflicts.foreach(validPaths => {
//         newConflicts = newConflicts ++ validPaths
//      })
//      newConflicts.filter(path => path.nonEmpty).map(node => node.toString)
   }

   def pathToJCE():String =
      System.getProperty("java.home") + File.separator + "lib" + File.separator + "jce.jar"

   def pathToRT():String =
      System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"

   // TODO: uncomment the code below if the new implementations do not work
//   def svgToDotModel(): String = {
//      val s = new StringBuilder
//      var nodeColor = ""
//      s ++= "digraph { \n"
//
//      for(n <- svg.nodes) {
//         nodeColor = n.nodeType match  {
//            case SourceNode => "[fillcolor=blue, style=filled]"
//            case SinkNode   => "[fillcolor=red, style=filled]"
//            case _          => ""
//         }
//         // TODO: uncomment the code below if the new implementations do not work
////         s ++= " " + "\"" + n.stmt + "\"" + nodeColor + "\n"
//         // TODO: remove the code below if the new implementations do not work
//         s ++= " " + "\"" + n.show() + "\"" + nodeColor + "\n"
//      }
//
//      for(n <- svg.nodes) {
//         val adjacencyList = svg.map.get(n).get
//         // TODO: uncomment the code below if the new implementations do not work
////         val edges = adjacencyList.map(next => "\"" + n.stmt + "\"" + " -> " + "\"" + next.stmt + "\"")
//         // TODO: remove the code below if the new implementations do not work
//         val edges = adjacencyList.map(next => "\"" + n.show() + "\"" + " -> " + "\"" + next.show() + "\"")
//         for(e <- edges) {
//            s ++= " " + e + "\n"
//         }
//      }
//
//      s ++= "}"
//
//      return s.toString()
//   }

   // TODO: remove this code
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

         s ++= " " + "\"" + n.show + "\"" + nodeColor + "\n"

         s ++= "\t{\n"

         val adjacencyList = svg.get(n)
         //      val edges = adjacencyList.map(next => "\"" + n.stmt + "\"" + " -> " + "\"" + next.stmt + "\"")
         val edges = adjacencyList.map(next => " -> " + "\"" + next.show + "\"")
         for(e <- edges) {
            s ++= "\t\t" + " " + e + "\n"
         }

         s ++= "\t}\n"
      }

      s ++= "}"

      return s.toString()
   }
}