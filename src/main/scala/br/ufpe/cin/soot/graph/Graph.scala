package br.ufpe.cin.soot.graph

import scalax.collection.edge.LDiEdge
import scala.collection.immutable.HashSet

sealed trait NodeType

case object SourceNode extends NodeType { def instance: SourceNode.type = this }
case object SinkNode extends NodeType { def instance: SinkNode.type = this }
case object SimpleNode extends NodeType { def instance: SimpleNode.type = this }

sealed trait EdgeType

case object CallSiteOpenEdge extends EdgeType { def instance: CallSiteOpenEdge.type = this }
case object CallSiteCloseEdge extends EdgeType { def instance: CallSiteCloseEdge.type = this }
case object SimpleEdge extends EdgeType { def instance: SimpleEdge.type = this }
case object ControlDependency extends EdgeType { def instance: ControlDependency.type = this }
case object ControlDependencyFalse extends EdgeType { def instance: ControlDependencyFalse.type = this }


case class Stmt(className: String, method: String, stmt: String, line: Int)

trait LambdaLabel {
  type T
  var value: T
  val edgeType: EdgeType
}

class StringLabel(label: String) extends LambdaLabel {
  override type T = String
  override var value: String = label
  override val edgeType: EdgeType = EdgeType.convert(label)
}

object EdgeType {
  def convert(edge: String): EdgeType = {
    if(edge.equals(ControlDependency.toString)) {
      ControlDependency
    } else if (edge.equals(ControlDependencyFalse.toString)) {
      ControlDependencyFalse
    }
    else SimpleEdge
  }
}

class CallSiteLabel(callSite: CallSite, callSiteType: EdgeType) extends LambdaLabel {
  override type T = CallSite
  override var value = callSite
  override val edgeType = callSiteType

  def matchCS(otherLabel: Any): Boolean = {
    otherLabel match {
      case defaultCSLabel: CallSiteLabel =>
        val csLabel = defaultCSLabel.value
        if ((this.edgeType == CallSiteOpenEdge && defaultCSLabel.edgeType == CallSiteCloseEdge) ||
          (defaultCSLabel.edgeType == CallSiteOpenEdge && this.edgeType == CallSiteCloseEdge)) {
          val matchClassName = this.value.className == csLabel.className
          val matchMethod = this.value.callerMethod == csLabel.callerMethod
          val matchStmt = this.value.stmt == csLabel.stmt
          val matchLine = this.value.line == csLabel.line
          return matchClassName && matchMethod && matchStmt && matchLine
        } else {
          return false
        }
      case _ => return false
    }
  }

  def matchCSMethod(otherLabel: Any): Boolean = {
    otherLabel match {
      case defaultCSLabel: CallSiteLabel =>
        val csLabel = defaultCSLabel.value
        // Match close with open OR open with close
        if ((this.edgeType == CallSiteOpenEdge && defaultCSLabel.edgeType == CallSiteCloseEdge) ||
          (defaultCSLabel.edgeType == CallSiteOpenEdge && this.edgeType == CallSiteCloseEdge)) {
          val matchCalleeMethod = this.value.calleeMethod == csLabel.calleeMethod
          return matchCalleeMethod
        } else {
          return false
        }
      case _ => return false
    }
  }

  override def equals(o: Any): Boolean = {
    o match {
      case csLabel: CallSiteLabel =>
        return this.value.equals(csLabel.value) && this.edgeType == csLabel.edgeType
      case _ => return false
    }
  }
}

case class CallSite(className: String, callerMethod: String, stmt: String, line: Int, sourceStmt: String,
                    calleeMethod: String)

trait LambdaNode {
  type T
  val value: T
  val nodeType: NodeType
  def show(): String
}

case class StmtNode(stmt: Stmt, stmtType: NodeType) extends LambdaNode {
  override type T = Stmt
  override val value: Stmt = stmt
  override val nodeType: NodeType = stmtType

  override def show(): String = value.stmt

  //  override def show(): String = "(" ++ value.method + ": " + value.stmt + " - " + value.line + " <" + nodeType.toString + ">)"

  override def toString: String =
    "Node(" + value.method + "," + value.stmt + "," + value.line.toString + "," + nodeType.toString + ")"

  override def equals(o: Any): Boolean = {
    o match {
      case stmt: StmtNode => stmt.value == value && stmt.nodeType == nodeType
      case _ => false
    }
  }

  override def hashCode(): Int = 2 * stmt.hashCode() + nodeType.hashCode()
}

class Graph() {
  val graph = scalax.collection.mutable.Graph.empty[LambdaNode, LDiEdge]

  var fullGraph: Boolean = false
  var allPaths: Boolean = false
  var optimizeGraph: Boolean = false

  def gNode(outerNode: LambdaNode): graph.NodeT = graph.get(outerNode)
  def gEdge(outerEdge: LDiEdge[LambdaNode]): graph.EdgeT = graph.get(outerEdge)

  def contains(node: LambdaNode): Boolean = {
    val graphNode = graph.find(node)
    if (graphNode.isDefined) {
      return true
    }
    return false
  }

  def addNode(node: LambdaNode): Unit = {
    graph.add(node)
  }

  def addEdge(source: LambdaNode, target: LambdaNode): Unit = {
    val label = new StringLabel("Normal")
    addEdge(source, target, label)
  }

  def addEdge(source: LambdaNode, target: LambdaNode, label: LambdaLabel): Unit = {
    if(source == target) {
      return
    }

    implicit val factory = scalax.collection.edge.LDiEdge
    graph.addLEdge(source, target)(label)
  }

  def getAdjacentNodes(node: LambdaNode): Option[Set[LambdaNode]] = {
    if (contains(node)) {
      return Some(gNode(node).diSuccessors.map(_node => _node.toOuter))
    }
    return None
  }

  def getIgnoredNodes(): HashSet[LambdaNode] = {
    var ignoredNodes = HashSet.empty[LambdaNode]
    var hasChanged = 51
    while (hasChanged > 50) {
      hasChanged = 0
      this.nodes().diff(ignoredNodes).foreach(n => {
        val gNode = this.gNode(n)
        val hasValidSuccessors = gNode.diSuccessors
          .exists(gSuccessor => !ignoredNodes(gSuccessor.toOuter))
        val hasValidPredecessors = gNode.diPredecessors
          .exists(gPredecessor => !ignoredNodes(gPredecessor.toOuter))

        if (hasValidSuccessors || hasValidPredecessors) {
          n.nodeType match {
            case SourceNode =>
              if (! hasValidSuccessors) {
                ignoredNodes = ignoredNodes + n
                hasChanged += 1
              }
            case SinkNode =>
              if (! hasValidPredecessors) {
                ignoredNodes = ignoredNodes + n
                hasChanged += 1
              }
            case _ =>
              if (!(hasValidPredecessors && hasValidSuccessors)) {
                ignoredNodes = ignoredNodes + n
                hasChanged += 1
              }
          }
        } else {
          ignoredNodes = ignoredNodes + n
          hasChanged += 1
        }
      })
    }

    return ignoredNodes
  }

  def findPathsFullGraph(): List[List[LambdaNode]] = {
    val ignoredNodes = getIgnoredNodes()

    if (this.optimizeGraph) {
      ignoredNodes.foreach(node => this.graph.remove(node))
      println("Optimize: " + ignoredNodes.size + " removed nodes.")
    }

    var sourceNodes = HashSet.empty[LambdaNode]
    var sinkNodes = HashSet.empty[LambdaNode]
    this.nodes().diff(ignoredNodes).foreach(n => {
      n.nodeType match {
        case SourceNode => sourceNodes = sourceNodes + n
        case SinkNode => sinkNodes = sinkNodes + n
        case _ => ()
      }
    })

    val maxConflictsNumber = sinkNodes.size
    var paths = List.empty[List[LambdaNode]]
    for(sourceNode <- sourceNodes; sinkNode <- sinkNodes) {
      if (paths.size >= maxConflictsNumber)
        return paths


      val foundedPaths = findPathsOP(sourceNode, sourceNode, sinkNode, HashSet(sourceNode), ignoredNodes, maxConflictsNumber)
      val validPaths = foundedPaths.filter(path => isValidPath(sourceNode, sinkNode, path))
      if (validPaths.nonEmpty)
        paths = paths ++ List(validPaths.head)
    }

    return paths
  }

  def findPathsOP(sourceNode: LambdaNode, currentNode: LambdaNode, sinkNode: LambdaNode, visitedNodes: HashSet[LambdaNode],
                  ignoredNodes: HashSet[LambdaNode], maxConflictsNumber: Int): List[List[LambdaNode]] = {
    var paths = List.empty[List[LambdaNode]]

    var validSuccessors = this.gNode(currentNode).diSuccessors
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
              findPathsOP(sourceNode, successor, sinkNode, visitedNodes + successor, ignoredNodes, maxConflictsNumber)
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
              findPathsOP(sourceNode, successor, sinkNode, visitedNodes + successor, ignoredNodes, maxConflictsNumber)
            paths = paths ++ foundedPaths
          }
        })
      }
    }

    return paths
  }

  def isValidPath(sourceNode: LambdaNode, sinkNode: LambdaNode, path: List[LambdaNode]): Boolean = {
    val gPath = this.gNode(sourceNode)
      .withSubgraph(node => path.contains(node.toOuter))
      .pathTo(this.gNode(sinkNode))
    return isValidPath(gPath.get)
  }

  def findPath(source: LambdaNode, target: LambdaNode): List[List[LambdaNode]] = {
    val fastPath = gNode(source).pathTo(gNode(target))
    val findAllConflictPaths = false

    if (! findAllConflictPaths && fastPath.isDefined && isValidPath(fastPath.get)) {
      return List(fastPath.get.nodes.map(node => node.toOuter).toList)
    }

    val pathBuilder = graph.newPathBuilder(gNode(source))
    val paths = findPaths(source, target, HashSet[LambdaNode](), pathBuilder, List())
    val validPaths = paths.filter(path => isValidPath(path))
    return validPaths.map(path => path.nodes.map(node => node.toOuter).toList)
  }

  def findPaths(source: LambdaNode, target: LambdaNode, visited: HashSet[LambdaNode],
                currentPath: graph.PathBuilder, paths: List[graph.Path]): List[graph.Path] = {
    // TODO: find some optimal way to travel in graph
    val adjacencyList = gNode(source).diSuccessors.map(_node => _node.toOuter)
    if (adjacencyList.contains(target)) {
      currentPath += gNode(target)
      //      return paths ++ List(currentPath.result)
      return List(currentPath.result)
    }

    adjacencyList.foreach(next => {
      if (! visited(next)) {
        var nextPath = currentPath
        nextPath += gNode(next)
        return findPaths(next, target, visited + next, nextPath, paths)
      }
    })
    return List()

    //    var possiblePaths = paths
    //    adjacencyList.foreach(next => {
    //      if (! visited(next)) {
    //        var nextPath = currentPath
    //        nextPath += gNode(next)
    //        val possiblePath = findPaths(next, target, visited + next, nextPath, paths)
    //        if (possiblePath.nonEmpty) {
    //          var findAllConflictPaths = false
    //          if (findAllConflictPaths) {
    //            possiblePaths = possiblePaths ++ possiblePath
    //          } else {
    //            return possiblePath
    //          }
    //        }
    //      }
    //    })
    //
    //    return possiblePaths
  }

  def isValidPath(path: graph.Path): Boolean = {
    val callSiteEdges = path.edges.map(edge => edge.toOuter).filter(edge => edge.label.isInstanceOf[CallSiteLabel])
    val edgeLabels = callSiteEdges.map(edge => edge.label.asInstanceOf[CallSiteLabel])
    val csOpen = edgeLabels.filter(label => label.edgeType == CallSiteOpenEdge)
    val csClose = edgeLabels.filter(label => label.edgeType == CallSiteCloseEdge)
    var unopenedCS: List[CallSiteLabel] = List()
    var unclosedCS: List[CallSiteLabel] = List()
    var csOpenUnvisited = csOpen
    var csCloseUnvisited = csClose

    // verify if exists cs) nodes without a (cs
    csClose.foreach(_csClose => {
      if (csOpenUnvisited.exists(label => label.matchCS(_csClose))) {
        val matchedCS = csOpenUnvisited.filter(label => label.matchCS(_csClose))
        val unmatchedCS = csOpenUnvisited.filter(label => ! label.matchCS(_csClose))
        if (matchedCS.size > 1) {
          csOpenUnvisited = unmatchedCS ++ matchedCS.init
        } else {
          csOpenUnvisited = unmatchedCS
        }
      } else {
        unopenedCS = unopenedCS ++ List(_csClose)
      }
    })

    // verify if exists (cs nodes without a cs)
    csOpen.foreach(_csOpen => {
      if (csCloseUnvisited.exists(label => label.matchCS(_csOpen))) {
        val matchedCS = csCloseUnvisited.filter(label => label.matchCS(_csOpen))
        val unmatchedCS = csCloseUnvisited.filter(label => ! label.matchCS(_csOpen))
        if (matchedCS.size > 1) {
          csCloseUnvisited = unmatchedCS ++ matchedCS.init
        } else {
          csCloseUnvisited = unmatchedCS
        }
      } else {
        unclosedCS = unclosedCS ++ List(_csOpen)
      }
    })

    // verify if the unopened and unclosed call-sites are not for the same method
    var matchedUnopenedUnclosedCSCalleeMethod: List[(CallSiteLabel, CallSiteLabel)] = List()
    unclosedCS.foreach(_csOpen => {
      unopenedCS.filter(label => label.matchCSMethod(_csOpen)).foreach(_csClose => {
        matchedUnopenedUnclosedCSCalleeMethod = matchedUnopenedUnclosedCSCalleeMethod ++ List((_csOpen, _csClose))
      })
    })

    if (unopenedCS.isEmpty || unclosedCS.isEmpty || matchedUnopenedUnclosedCSCalleeMethod.isEmpty) {
      return true
    }
    return false
  }

  def nodes(): scala.collection.Set[LambdaNode] = graph.nodes.map(node => node.toOuter).toSet

  def numberOfNodes(): Int = graph.nodes.size

  def numberOfEdges(): Int = graph.edges.size
}
