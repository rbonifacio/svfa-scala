package br.unb.cic.soot.svfa

import br.unb.cic.soot.graph.Node
import scalax.collection
import scalax.collection.GraphEdge.{DiEdge}
import soot._
import soot.options.Options

import scala.collection.JavaConverters._

/**
  * Base class for all implementations
  * of SVFA algorithms.
  */
abstract class SVFA {

   protected var pointsToAnalysis : PointsToAnalysis = _
   var svg = collection.mutable.Graph.empty[Node,DiEdge]

   def sootClassPath(): String
   def applicationClassPath(): List[String]
   def getEntryPoints(): List[SootMethod]
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
      Options.v().set_no_bodies_for_excluded(true)
      Options.v().set_allow_phantom_refs(true)
      Options.v().set_keep_line_number(true)
      Options.v().set_prepend_classpath(true)
      Options.v().set_soot_classpath(sootClassPath())
      Options.v().set_process_dir(applicationClassPath().asJava)
      Options.v().set_full_resolver(true)
      Scene.v().loadNecessaryClasses()
      Scene.v().setEntryPoints(getEntryPoints().asJava)
   }

}