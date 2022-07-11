package br.unb.cic.soot.svfa

import br.unb.cic.soot.graph.{CallSiteLabel, CallSiteOpenLabel, GraphNode, SinkNode, SourceNode, StatementNode, StringLabel}
import soot._


/**
 * Base class for all implementations
 * of SootConfiguration algorithms.
 */
abstract class SVFA extends  SootConfiguration{

  var svg = new br.unb.cic.soot.graph.Graph()

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
        case _ => ""
      }

      s ++= " " + edge + " " + label + "\n"
    }

    s ++= "}"

    return s.toString()
  }


}
