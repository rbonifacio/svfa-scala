package br.unb.cic.soot.graph

import scalax.collection.edge.LkDiEdge
import soot.SootMethod

import scala.collection.immutable.HashSet

/*
 * This trait define the base type for node classifications.
 * A node can be classified as SourceNode, SinkNode or SimpleNode.
 */
sealed trait NodeType

case object SourceNode extends NodeType { def instance: SourceNode.type = this }
case object SinkNode extends NodeType { def instance: SinkNode.type = this }
case object SimpleNode extends NodeType { def instance: SimpleNode.type = this }

/*
 * This trait define the abstraction needed to possibility custom node types,
 * acting as a container to hold the node data inside the value attribute.
 * The attribute nodeType hold the node classification as source, sink or simple.
 */
trait GraphNode {
  type T
  val value: T
  val nodeType: NodeType
  def unit(): soot.Unit
  def method(): soot.SootMethod
  def show(): String
}

trait LambdaNode extends scala.AnyRef {
  type T
  val value: LambdaNode.this.T
  val nodeType: br.unb.cic.soot.graph.NodeType
  def show(): _root_.scala.Predef.String
}

/*
 * Simple class to hold all the information needed about a statement,
 * this value is stored in the value attribute of the GraphNode. For the most cases,
 * it is enough for the analysis, but for some situations, something
 * specific for Jimple or Shimple abstractions can be a better option.
 */
case class Statement(
    className: String,
    method: String,
    stmt: String,
    line: Int,
    sootUnit: soot.Unit = null,
    sootMethod: soot.SootMethod = null
)

/*
 * A graph node defined using the GraphNode abstraction specific for statements.
 * Use this class as example to define your own custom nodes.
 */
case class StatementNode(value: Statement, nodeType: NodeType)
    extends GraphNode {
  type T = Statement

  //  override def show(): String = "(" ++ value.method + ": " + value.stmt + " - " + value.line + " <" + nodeType.toString + ">)"
  override def show(): String = value.stmt.replaceAll("\"", "'")

  override def toString: String =
    "Node(" + value.method + "," + value.stmt + "," + "," + nodeType.toString + ")"

  override def equals(o: Any): Boolean = {
    o match {
      //      case stmt: StatementNode => stmt.value.toString == value.toString
      //      case stmt: StatementNode => stmt.value == value && stmt.nodeType == nodeType
      case stmt: StatementNode =>
        stmt.value.className.equals(value.className) &&
        stmt.value.method.equals(value.method) &&
        stmt.value.stmt.equals(value.stmt) &&
        stmt.value.line.equals(value.line) &&
        stmt.nodeType.equals(nodeType)
      case _ => false
    }
  }

  override def hashCode(): Int = 2 * value.hashCode() + nodeType.hashCode()

  override def unit(): soot.Unit = value.sootUnit

  override def method(): SootMethod = value.sootMethod
}

/*
 * This trait define the base for all other labels classifications, like the NodeType
 * the LabelType is used to inform things relevant for the analysis like context sensitive
 * regions or field sensitive actions (store or load).
 */
trait LabelType

case object SimpleLabel extends LabelType {
  def instance: SimpleLabel.type = this
}

sealed trait CallSiteLabelType extends LabelType
case object CallSiteOpenLabel extends CallSiteLabelType {
  def instance: CallSiteOpenLabel.type = this
}
case object CallSiteCloseLabel extends CallSiteLabelType {
  def instance: CallSiteCloseLabel.type = this
}

/*
 * Like the graph nodes, the edge labels can be customized and this trait
 * define the abstraction needed to possibility the customization,
 * acting as a container to hold the labels data inside the value attribute.
 */
trait EdgeLabel {
  type T
  var value: T
  val labelType: LabelType
}

case class StringLabel(label: String) extends EdgeLabel {
  override type T = String
  override var value = label
  override val labelType: LabelType = SimpleLabel
}

case class ContextSensitiveRegion(statement: Statement, calleeMethod: String, context: Set[String])

case class CallSiteLabel(
    csRegion: ContextSensitiveRegion,
    labelType: CallSiteLabelType
) extends EdgeLabel {
  override type T = ContextSensitiveRegion
  override var value = csRegion

  def matchCallStatement(otherLabel: Any): Boolean = {
    otherLabel match {
      case defaultCSLabel: CallSiteLabel =>
        val csLabel = defaultCSLabel.value
        // Match close with open OR open with close
        if (labelType != defaultCSLabel.labelType) {
          return value.statement == csLabel.statement
        } else {
          false
        }
      case _ => false
    }
  }

  def matchCalleeMethod(otherLabel: Any): Boolean = {
    otherLabel match {
      case defaultCSLabel: CallSiteLabel =>
        val csLabel = defaultCSLabel.value
        // Match close with open OR open with close
        if (labelType != defaultCSLabel.labelType) {
          return value.calleeMethod == csLabel.calleeMethod
        } else {
          false
        }
      case _ => false
    }
  }

  override def equals(o: Any): Boolean = {
    o match {
      case csLabel: CallSiteLabel =>
        return value == csLabel.value && labelType == csLabel.labelType
      case _ => false
    }
  }
}

case class GraphEdge(from: GraphNode, to: GraphNode, label: EdgeLabel)

class Graph() {
  val graph = scalax.collection.mutable.Graph.empty[GraphNode, LkDiEdge]

  var fullGraph: Boolean = false
  var allPaths: Boolean = false
  var optimizeGraph: Boolean = false
  var permitedReturnEdge: Boolean = false

  def enableReturnEdge(): Unit = {
    permitedReturnEdge = true
  }

  def gNode(outerNode: GraphNode): graph.NodeT = graph.get(outerNode)
  def gEdge(outerEdge: LkDiEdge[GraphNode]): graph.EdgeT = graph.get(outerEdge)

  def contains(node: GraphNode): Boolean = graph.find(node).isDefined

  def addNode(node: GraphNode): Unit = graph.add(node)

  def addEdge(source: GraphNode, target: GraphNode): Unit =
    addEdge(source, target, StringLabel("Normal"))

  def addEdge(source: StatementNode, target: StatementNode): Unit =
    addEdge(source, target, StringLabel("Normal"))

  def addEdge(source: GraphNode, target: GraphNode, label: EdgeLabel): Unit = {
    if (source == target && !permitedReturnEdge) {
      return
    }

    implicit val factory = scalax.collection.edge.LkDiEdge
    graph.addLEdge(source, target)(label)
  }

  def addEdge(
      source: StatementNode,
      target: StatementNode,
      label: EdgeLabel
  ): Unit = {
    if (source == target && !permitedReturnEdge) {
      return
    }

    implicit val factory = scalax.collection.edge.LkDiEdge
    graph.addLEdge(source, target)(label)
  }

  def getAdjacentNodes(node: GraphNode): Option[Set[GraphNode]] = {
    if (contains(node)) {
      return Some(gNode(node).diSuccessors.map(_node => _node.toOuter))
    }
    return None
  }

  def getIgnoredNodes(): HashSet[GraphNode] = {
    var ignoredNodes = HashSet.empty[GraphNode]
    var countChanges = 51
    while (countChanges > 50) {
      countChanges = 0
      this
        .nodes()
        .diff(ignoredNodes)
        .foreach(n => {
          val gNode = this.gNode(n)
          val hasValidSuccessors = gNode.diSuccessors
            .exists(gSuccessor => !ignoredNodes(gSuccessor.toOuter))
          val hasValidPredecessors = gNode.diPredecessors
            .exists(gPredecessor => !ignoredNodes(gPredecessor.toOuter))

          if (hasValidSuccessors || hasValidPredecessors) {
            n.nodeType match {
              case SourceNode =>
                if (!hasValidSuccessors) {
                  ignoredNodes = ignoredNodes + n
                  countChanges += 1
                }
              case SinkNode =>
                if (!hasValidPredecessors) {
                  ignoredNodes = ignoredNodes + n
                  countChanges += 1
                }
              case _ =>
                if (!(hasValidPredecessors && hasValidSuccessors)) {
                  ignoredNodes = ignoredNodes + n
                  countChanges += 1
                }
            }
          } else {
            ignoredNodes = ignoredNodes + n
            countChanges += 1
          }
        })
    }

    return ignoredNodes
  }

  def findPathsFullGraph(): List[List[GraphNode]] = {
    val ignoredNodes = getIgnoredNodes()

    if (this.optimizeGraph) {
      ignoredNodes.foreach(node => this.graph.remove(node))
      println("Optimize: " + ignoredNodes.size + " removed nodes.")
    }

    var sourceNodes = HashSet.empty[GraphNode]
    var sinkNodes = HashSet.empty[GraphNode]
    this
      .nodes()
      .diff(ignoredNodes)
      .foreach(n => {
        n.nodeType match {
          case SourceNode => sourceNodes = sourceNodes + n
          case SinkNode   => sinkNodes = sinkNodes + n
          case _          => ()
        }
      })

    val maxConflictsNumber = sinkNodes.size
    var paths = List.empty[List[GraphNode]]
    for (sourceNode <- sourceNodes; sinkNode <- sinkNodes) {
      if (paths.size >= maxConflictsNumber)
        return paths

      val foundedPaths = findPathsOP(
        sourceNode,
        sourceNode,
        sinkNode,
        HashSet(sourceNode),
        ignoredNodes,
        maxConflictsNumber
      )
      val validPaths =
        foundedPaths.filter(path => isValidPath(sourceNode, sinkNode, path))
      if (validPaths.nonEmpty)
        paths = paths ++ List(validPaths.head)
    }

    return paths
  }

  def findPathsOP(
      sourceNode: GraphNode,
      currentNode: GraphNode,
      sinkNode: GraphNode,
      visitedNodes: HashSet[GraphNode],
      ignoredNodes: HashSet[GraphNode],
      maxConflictsNumber: Int
  ): List[List[GraphNode]] = {
    var paths = List.empty[List[GraphNode]]

    var validSuccessors = this
      .gNode(currentNode)
      .diSuccessors
      .map(gSuccessor => gSuccessor.toOuter)

    if (this.optimizeGraph) {
      validSuccessors = validSuccessors.diff(ignoredNodes)
    }

    validSuccessors = validSuccessors.diff(visitedNodes)

    if (allPaths) {
      validSuccessors.foreach(successor => {
        if (paths.size < maxConflictsNumber) {
          if (successor == sinkNode) {
            val path = (visitedNodes + successor).toList
            paths = paths ++ List(path)
          } else {
            val foundedPaths =
              findPathsOP(
                sourceNode,
                successor,
                sinkNode,
                visitedNodes + successor,
                ignoredNodes,
                maxConflictsNumber
              )
            paths = paths ++ foundedPaths
          }
        }
      })
    } else {
      if (validSuccessors.contains(sinkNode)) {
        val path = (visitedNodes + sinkNode).toList
        if (isValidPath(sourceNode, sinkNode, path)) {
          paths = paths ++ List(path)
        }
      } else {
        validSuccessors.foreach(successor => {
          if (paths.size < maxConflictsNumber) {
            val foundedPaths =
              findPathsOP(
                sourceNode,
                successor,
                sinkNode,
                visitedNodes + successor,
                ignoredNodes,
                maxConflictsNumber
              )
            paths = paths ++ foundedPaths
          }
        })
      }
    }

    return paths
  }

  def isValidPath(
      sourceNode: GraphNode,
      sinkNode: GraphNode,
      path: List[GraphNode]
  ): Boolean = {
    val gPath = this
      .gNode(sourceNode)
      .withSubgraph(node => path.contains(node.toOuter))
      .pathTo(this.gNode(sinkNode))
    return isValidPath(gPath.get)
  }

  def findPath(source: GraphNode, target: GraphNode): List[List[GraphNode]] = {
    val fastPath = gNode(source).pathTo(gNode(target))
    val findAllConflictPaths = false

    if (
      !findAllConflictPaths && fastPath.isDefined && isValidPath(fastPath.get)
    ) {
      return List(fastPath.get.nodes.map(node => node.toOuter).toList)
    }

    val pathBuilder = graph.newPathBuilder(gNode(source))
    val paths =
      findPaths(source, target, HashSet[GraphNode](), pathBuilder, List())
    val validPaths = paths.filter(path => isValidPath(path))
    return validPaths.map(path => path.nodes.map(node => node.toOuter).toList)
  }

  def findPaths(
      source: GraphNode,
      target: GraphNode,
      visited: HashSet[GraphNode],
      currentPath: graph.PathBuilder,
      paths: List[graph.Path]
  ): List[graph.Path] = {
    // TODO: find some optimal way to travel in graph
    val adjacencyList = gNode(source).diSuccessors.map(_node => _node.toOuter)
    if (adjacencyList.contains(target)) {
      currentPath += gNode(target)
      //      return paths ++ List(currentPath.result)
      return List(currentPath.result)
    }

    adjacencyList.foreach(next => {
      if (!visited(next)) {
        var nextPath = currentPath
        nextPath += gNode(next)
        return findPaths(next, target, visited + next, nextPath, paths)
      }
    })
    return List()

  }

  def getUnmatchedCallSites(
      source: List[CallSiteLabel],
      target: List[CallSiteLabel]
  ): List[CallSiteLabel] = {
    var unvisitedTargets = target
    var unmatched = List.empty[CallSiteLabel]

    // verify if exists cs) edges without a (cs
    // or if exists (cs edges without a cs)
    source.foreach(s => {
      var matchedCS = List.empty[CallSiteLabel]
      var unmatchedCS = List.empty[CallSiteLabel]
      unvisitedTargets.foreach(label => {
        if (label.matchCallStatement(s))
          matchedCS = matchedCS ++ List(label)
        else
          unmatchedCS = unmatchedCS ++ List(label)
      })

      if (matchedCS.size > 0) {
        unvisitedTargets = unmatchedCS ++ matchedCS.init
      } else {
        unvisitedTargets = unmatchedCS
        unmatched = unmatched ++ List(s)
      }
    })

    return unmatched
  }

  def isValidPath(path: graph.Path): Boolean = {
    var csOpen = List.empty[CallSiteLabel]
    var csClose = List.empty[CallSiteLabel]

    // Filter the labels by type
    path.edges.foreach(edge => {
      val label = edge.toOuter.label

      label match {
        case l: CallSiteLabel => {
          if (l.labelType == CallSiteOpenLabel)
            csOpen = csOpen ++ List(l)
          else
            csClose = csClose ++ List(l)
        }
        case _ => {}
      }
    })

    // check if there path has only one context
    if (! isValidContext(csOpen, csClose)) {
        return false
    }

    // Get all the cs) without a (cs
    val unopenedCS = getUnmatchedCallSites(csClose, csOpen)
    // Get all the cs) without a (cs
    val unclosedCS = getUnmatchedCallSites(csOpen, csClose)

    // verify if the unopened and unclosed call-sites are not for the same method
    var matchedUnopenedUnclosedCSCalleeMethod
        : List[(CallSiteLabel, CallSiteLabel)] = List()
    unclosedCS.foreach(_csOpen => {
      unopenedCS
        .filter(label => label.matchCalleeMethod(_csOpen))
        .foreach(_csClose => {
          matchedUnopenedUnclosedCSCalleeMethod =
            matchedUnopenedUnclosedCSCalleeMethod ++ List((_csOpen, _csClose))
        })
    })

    val validCS =
      unopenedCS.isEmpty || unclosedCS.isEmpty || matchedUnopenedUnclosedCSCalleeMethod.isEmpty

    return validCS
  }

  def isValidContext(csOpen: List[CallSiteLabel], csClose: List[CallSiteLabel]): Boolean = {
    var cs: Set[String] = Set()

    val csOpenAndClose = csOpen ++ csClose

    csOpenAndClose.foreach(open => {
      if (open.value.context.nonEmpty) {
        cs = cs + open.value.context.head
      }
    })

    cs.size <= 1
  }

  def nodes(): scala.collection.Set[GraphNode] = graph.nodes.map(node => node.toOuter).toSet

  def edges(): scala.collection.Set[GraphEdge] = graph.edges
    .map(edge => {
      val from = edge._1.toOuter
      val to = edge._2.toOuter
      val label = edge.toOuter.label.asInstanceOf[EdgeLabel]

      GraphEdge(from, to, label)
    })
    .toSet

  def numberOfNodes(): Int = graph.nodes.size

  def numberOfEdges(): Int = graph.edges.size
  /*
   * creates a graph node from a sootMethod / sootUnit
   */
  def createNode(
      method: SootMethod,
      stmt: soot.Unit,
      f: (soot.Unit) => NodeType
  ): StatementNode =
    StatementNode(
      br.unb.cic.soot.graph.Statement(
        method.getDeclaringClass.toString,
        method.getSignature,
        stmt.toString,
        stmt.getJavaSourceStartLineNumber,
        stmt,
        method
      ),
      f(stmt)
    )

  def reportConflicts(): scala.collection.Set[String] =
    findConflictingPaths().map(p => p.toString)

  def findConflictingPaths(): scala.collection.Set[List[GraphNode]] = {
    if (fullGraph) {
      val conflicts = findPathsFullGraph()
      conflicts.toSet
    } else {
      val sourceNodes = nodes.filter(n => n.nodeType == SourceNode)
      val sinkNodes = nodes.filter(n => n.nodeType == SinkNode)

      var conflicts: List[List[GraphNode]] = List()
      sourceNodes.foreach(source => {
        sinkNodes.foreach(sink => {
          val paths = findPath(source, sink)
          // show paths
//          paths.foreach(path => {
//            println("[path]")
//            path.foreach(println(_))
//          })
          conflicts = conflicts ++ paths
        })
      })
      conflicts.filter(p => p.nonEmpty).toSet
    }
  }

  def toDotModel(): String = {
    val s = new StringBuilder
    var nodeColor = ""
    s ++= "digraph { \n"

    for (n <- nodes) {
      nodeColor = n.nodeType match {
        case SourceNode => "[fillcolor=blue, style=filled]"
        case SinkNode   => "[fillcolor=red, style=filled]"
        case _          => ""
      }

      s ++= " " + "\"" + n.show() + "\"" + nodeColor + "\n"
    }

    s ++= "\n"

    for (e <- edges) {
      val edge =
        "\"" + e.from.show() + "\"" + " -> " + "\"" + e.to.show() + "\""
      var l = e.label
      val label: String = e.label match {
        case c: CallSiteLabel =>  {
          if (c.labelType == CallSiteOpenLabel) { s"""[label="CS([${ if (c.value.context.nonEmpty) c.value.context.head }]"]""" }
          else { s"""[label="CS)[${ if (c.value.context.nonEmpty) c.value.context.head }]"]""" }
//          if (c.labelType == CallSiteOpenLabel) { s"""[label="CS(${c.value.statement.stmt} [${c.value.context.head}]"]""" }
//          else { s"""[label="CS)${c.value.statement.stmt} [${c.value.context.head}]"]""" }
        }
        case c: TrueLabelType  => { "[penwidth=3][label=\"T\"]" }
        case c: FalseLabelType => { "[penwidth=3][label=\"F\"]" }
        case c: DefLabelType   => { "[style=dashed, color=black]" }
        case _                 => ""
      }
      s ++= " " + edge + " " + label + "\n"
    }
    s ++= "}"
    s.toString()
  }

}
