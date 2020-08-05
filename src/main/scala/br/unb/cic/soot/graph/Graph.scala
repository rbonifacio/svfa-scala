package br.unb.cic.soot.graph

import scalax.collection.edge.LDiEdge
import scalax.collection.mutable.Graph

sealed trait NodeType

case object SourceNode extends NodeType { def instance: SourceNode.type = this }
case object SinkNode extends NodeType { def instance: SinkNode.type = this }
case object SimpleNode extends NodeType { def instance: SimpleNode.type = this }
case object CallSiteNode extends NodeType { def instance: CallSiteNode.type = this }

sealed trait EdgeType

case object CallSiteOpenEdge extends EdgeType { def instance: CallSiteOpenEdge.type = this }
case object CallSiteCloseEdge extends EdgeType { def instance: CallSiteCloseEdge.type = this }

case class Stmt(className: String, method: String, stmt: String, line: Int)

trait LambdaLabel {
  type T
  var value: T
}

class StringLabel(label: String) extends LambdaLabel {
  override type T = String
  override var value: String = label
}

abstract class CallSiteLabel extends LambdaLabel {
  def isCallSiteOpen(): Boolean
  def isCallSiteClose(): Boolean
  def matchCS(label: CallSiteLabel): Boolean
  def matchCSMethod(label: CallSiteLabel): Boolean
}

case class CallSiteData(className: String, callerMethod: String, stmt: String, line: Int, sourceStmt: String,
                    calleeMethod: String, callSiteType: EdgeType)

class DefaultCallSiteLabel(label: CallSiteData) extends CallSiteLabel {
  override type T = CallSiteData
  override var value: CallSiteData = label

  override def isCallSiteOpen(): Boolean = value.callSiteType == CallSiteOpenEdge
  override def isCallSiteClose(): Boolean = value.callSiteType == CallSiteCloseEdge

  override def matchCS(label: CallSiteLabel): Boolean = {
    if (label.isInstanceOf[DefaultCallSiteLabel]) {
      val csLabel = label.asInstanceOf[DefaultCallSiteLabel].value
      if ((value.callSiteType == CallSiteOpenEdge && csLabel.callSiteType == CallSiteCloseEdge) ||
        (csLabel.callSiteType == CallSiteOpenEdge && value.callSiteType == CallSiteCloseEdge)) {
        val matchClassName = value.className == csLabel.className
        val matchMethod = value.callerMethod == csLabel.callerMethod
        val matchStmt = value.stmt == csLabel.stmt
        val matchLine = value.line == csLabel.line
        return matchClassName && matchMethod && matchStmt && matchLine
      }
    }
    return false
  }

  override def matchCSMethod(label: CallSiteLabel): Boolean = {
    if (label.isInstanceOf[DefaultCallSiteLabel]) {
      val csLabel = label.asInstanceOf[DefaultCallSiteLabel].value
      // Match close with open OR open with close
      if ((value.callSiteType == CallSiteOpenEdge && csLabel.callSiteType == CallSiteCloseEdge) ||
        (csLabel.callSiteType == CallSiteOpenEdge && value.callSiteType == CallSiteCloseEdge)) {
        val matchCalleeMethod = value.calleeMethod == csLabel.calleeMethod
        return matchCalleeMethod
      }
    }
    return false
  }

  override def equals(o: Any): Boolean = {
    if (label.isInstanceOf[DefaultCallSiteLabel]) {
      val csLabel = label.asInstanceOf[DefaultCallSiteLabel]
      return csLabel.value == value
    }
    return false
  }
}


trait LambdaNode {
  type T
  var value: T
  // TODO: make nodeType parametric too
  var nodeType: NodeType

  // Output to text function
  def show(): String
}

case class StmtNode(stmt: Stmt, stmtType: NodeType) extends LambdaNode {
  override type T = Stmt
  override var value: Stmt = stmt
  override var nodeType: NodeType = stmtType

  override def show(): String = "(" ++ value.method + ": " + value.stmt + " - " + value.line + " <" + nodeType.toString + ">)"

  override def toString: String =
    "Node(" + value.method + "," + value.stmt + "," + value.line.toString + "," + nodeType.toString + ")"

  override def equals(o: Any): Boolean = {
    if (o.isInstanceOf[StmtNode]) {
      val stmt = o.asInstanceOf[StmtNode]
      return stmt.value == value && stmt.nodeType == nodeType
    }
    return false
  }
}

class Graph() {
  val graph = Graph.empty[LambdaNode, LDiEdge]

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

  def getAdjacentNodes(node: LambdaNode): Option[Set[LambdaNode]] = {
    if (contains(node)) {
      return Some(gNode(node).diSuccessors.map(_node => _node.toOuter))
    }
    return None
  }

  def addEdge(source: LambdaNode, target: LambdaNode, label: LambdaLabel): Unit = {
    if(source == target) {
      return
    }

    implicit val factory = scalax.collection.edge.LDiEdge
    graph.addLEdge(source, target)(label)
  }

  def findPath(source: LambdaNode, target: LambdaNode): List[List[LambdaNode]] = {
    val fastPath = gNode(source).pathTo(gNode(target))
    var findAllConflitPaths = false

    if (! findAllConflitPaths && fastPath.isDefined && isValidPath(fastPath.get)) {
      return List(fastPath.get.nodes.map(node => node.toOuter).toList)
    }

    val pathBuilder = graph.newPathBuilder(gNode(source))
    val paths = findPaths(source, target, List(), pathBuilder, List())
    return paths.map(path => path.nodes.map(node => node.toOuter).toList)
  }

  def findPaths(source: LambdaNode, target: LambdaNode, visited: List[LambdaNode],
               currentPath: graph.PathBuilder, paths: List[graph.Path]): List[graph.Path] = {
    // TODO: find some optimal way to travel in graph
    val adjacencyList = gNode(source).diSuccessors.map(_node => _node.toOuter)
    if (adjacencyList.contains(target)) {
      currentPath += gNode(target)
      if (isValidPath(currentPath.result)) {
        return List(currentPath.result)
      }
    }

    var possiblePaths = paths
    adjacencyList.foreach(next => {
      if (! visited.contains(next)) {
        var nextPath = currentPath
        nextPath += gNode(next)
        val possiblePath = findPaths(next, target, visited ++ List(next), nextPath, paths)
        if (possiblePath.nonEmpty) {
          var findAllConflitPaths = false
          if (findAllConflitPaths) {
            possiblePaths = possiblePaths ++ possiblePath
          } else {
            return possiblePath
          }
        }
      }
    })
    return possiblePaths
  }

  def isValidPath(path: graph.Path): Boolean = {
    val callSiteEdges = path.edges.map(edge => edge.toOuter).filter(edge => edge.label.isInstanceOf[CallSiteLabel])
    val edgeLabels = callSiteEdges.map(edge => edge.label.asInstanceOf[CallSiteLabel])
    val csOpen = edgeLabels.filter(label => label.isCallSiteOpen())
    val csClose = edgeLabels.filter(label => label.isCallSiteClose())
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

    // verify if the unopened and unclosed callsites are not for the same method
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
