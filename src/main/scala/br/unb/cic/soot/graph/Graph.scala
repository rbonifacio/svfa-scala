package br.unb.cic.soot.graph

import scala.collection.mutable

sealed trait NodeType

case object SourceNode extends NodeType { def instance: SourceNode.type = this }
case object SinkNode extends NodeType { def instance: SinkNode.type = this }
case object SimpleNode extends NodeType { def instance: SimpleNode.type = this }
// TODO: remove this methods if is not working
case object CallSiteOpenNode extends NodeType { def instance: CallSiteOpenNode.type = this }
case object CallSiteCloseNode extends NodeType { def instance: CallSiteCloseNode.type = this }

case class Node(className: String, method: String, stmt: String, line: Int, nodeType: NodeType)


case class CallSite(className: String, callerMethod: String, stmt: String, sourceStmt: String, line: Int,
                    calleeMethod: String)
case class Stmt(className: String, method: String, stmt: String, line: Int)


class LambdaCallSite() extends LambdaNode {
  override type T = CallSite
  override var value: CallSite = _
  override var nodeType: NodeType = _

  override def set(_value: CallSite, _type: NodeType): Unit = {
    value = _value
    nodeType = _type
  }

  override def setValue(_value: CallSite): Unit = value = _value

  override def setType(_type: NodeType): Unit = nodeType = _type

  override def show(): String = "(" ++ value.callerMethod + ": " + " [" + value.sourceStmt + "] " + value.stmt + " - " + value.line + " <" + nodeType.toString + ">)"

  override def toString: String =
    "Node(" + value.callerMethod + "," + value.stmt + "," + value.line.toString + "," + nodeType.toString + ")"

  override def areEqualsTo(_node: LambdaNode): Boolean = {
    if (_node.isInstanceOf[LambdaCallSite]) {
      return haveSameTypeTo(_node) && haveSameValueTo(_node)
    }
    return false
  }

  override def haveSameValueTo(_node: LambdaNode): Boolean = {
    if (_node.value.isInstanceOf[CallSite]) {
      val callSite = _node.value.asInstanceOf[CallSite]
      if (callSite == value) {
        return true
      }
    }
    return false
  }

  override def haveSameTypeTo(_node: LambdaNode): Boolean = {
    if (_node.nodeType == nodeType) {
      return true
    }
    return false
  }

  override def matchCS(_node: LambdaNode): Boolean = {
    if (_node.isInstanceOf[LambdaCallSite]) {
      val callSite = _node.asInstanceOf[LambdaCallSite]
      // Match close with open OR open with close
      if ((nodeType == CallSiteCloseNode && callSite.nodeType == CallSiteOpenNode) ||
          (nodeType == CallSiteOpenNode && callSite.nodeType == CallSiteCloseNode)) {
        val matchClassName = value.className == callSite.value.className
        val matchMethod = value.callerMethod == callSite.value.callerMethod
        val matchStmt = value.stmt == callSite.value.stmt
        val matchLine = value.line == callSite.value.line
        return matchClassName && matchMethod && matchStmt && matchLine
      }
    }
    return false
  }

  override def matchCSMethod(_node: LambdaNode): Boolean = {
    if (_node.isInstanceOf[LambdaCallSite]) {
      val callSite = _node.asInstanceOf[LambdaCallSite]
      // Match close with open OR open with close
      if ((nodeType == CallSiteCloseNode && callSite.nodeType == CallSiteOpenNode) ||
        (nodeType == CallSiteOpenNode && callSite.nodeType == CallSiteCloseNode)) {
        val matchCalleeMethod = value.calleeMethod == callSite.value.calleeMethod
        return matchCalleeMethod
      }
    }
    return false
  }
}

class LambdaStmt() extends LambdaNode {
  override type T = Stmt
  override var value: Stmt = _
  override var nodeType: NodeType = _

  override def set(_value: Stmt, _type: NodeType): Unit = {
    value = _value
    nodeType = _type
  }

  override def setValue(_value: Stmt): Unit = value = _value

  override def setType(_type: NodeType): Unit = nodeType = _type

  override def show(): String = "(" ++ value.method + ": " + value.stmt + " - " + value.line + " <" + nodeType.toString + ">)"

  override def toString: String =
    "Node(" + value.method + "," + value.stmt + "," + value.line.toString + "," + nodeType.toString + ")"

  override def areEqualsTo(_node: LambdaNode): Boolean = {
    if (_node.isInstanceOf[LambdaStmt]) {
      return haveSameTypeTo(_node)  && haveSameValueTo(_node)
    }
    return false
  }

  override def haveSameValueTo(_node: LambdaNode): Boolean = {
    if (_node.value.isInstanceOf[Stmt]) {
      val stmt = _node.value.asInstanceOf[Stmt]
      if (stmt == value) {
        return true
      }
    }
    return false
  }

  override def haveSameTypeTo(_node: LambdaNode): Boolean = {
    if (_node.nodeType == nodeType) {
      return true
    }
    return false
  }

  override def matchCS(_node: LambdaNode): Boolean = false

  override def matchCSMethod(_node: LambdaNode): Boolean = false
}

trait LambdaNode {
  type T
  var value: T
  // TODO: make nodeType parametric too
  var nodeType: NodeType

  // Define functions
  def set(_value: T, _type: NodeType): Unit
  def setValue(_value: T): Unit
  def setType(_type: NodeType): Unit

  // Output to text function
  def show(): String

  // Compare functions
  def areEqualsTo(_node: LambdaNode): Boolean
  def haveSameValueTo(_node: LambdaNode): Boolean
  def haveSameTypeTo(_node: LambdaNode): Boolean

  // This function is necessary because the (cs and cs) are nodes and not edges
  // Nodes that don't are for callsite do not need implement this method
  def matchCS(_node: LambdaNode): Boolean
  def matchCSMethod(_node: LambdaNode): Boolean
}

class Graph() {
  private val map = new mutable.HashMap[LambdaNode,mutable.Set[LambdaNode]]()

  def contains(node: LambdaNode): Boolean =
    map.exists(pair => pair._1.areEqualsTo(node))

  def get(node: LambdaNode): mutable.Set[LambdaNode] = {
    val pairs = map.filter(pair => pair._1.areEqualsTo(node))
    if (pairs.nonEmpty) {
      return pairs.head._2
    }
    return mutable.Set.empty[LambdaNode]
  }

  def createNode(node: LambdaNode, edges: mutable.Set[LambdaNode]): Unit = {
    if (! contains(node)) {
      map(node) = edges
    }
  }

  def addEdge(source: LambdaNode, target: LambdaNode): Unit = {
    if(source.areEqualsTo(target)) {
      return
    }


    if (! contains(source)) {
      createNode(source, mutable.Set(target))
    } else {
      // TODO: verify if the map(node) are really creating the node in the map
      var adjacentList = get(source)
      adjacentList += target
    }

    if (! contains(target)) {
      // TODO: verify if the map(node) are really creating the node in the map
      createNode(target, mutable.Set.empty[LambdaNode])
    }
  }
  // TODO: uncomment the code below if the new implementations do not work
  def findPath(source: LambdaNode, target: LambdaNode): Option[List[LambdaNode]] =
    findPath(source, target, List(), List(source))
  // TODO: remove the code below if the new implementations do not work
//  def findPath(source: LambdaNode, target: LambdaNode): List[List[LambdaNode]] =
//    findPath(source, target, List(), List(source), List())

  // TODO: uncomment the code below if the new implementations do not work
  def findPath(source: LambdaNode, target: LambdaNode, visited: List[LambdaNode],
               path: List[LambdaNode]): Option[List[LambdaNode]] = {
    // TODO: remove the code below if the new implementations do not work
//  def findPath(source: LambdaNode, target: LambdaNode, visited: List[LambdaNode],
//               path: List[LambdaNode], validPaths: List[List[LambdaNode]]): List[List[LambdaNode]] = {

    val adjacentList = get(source)
    // TODO: uncomment the code below if the new implementations do not work
    if(adjacentList.exists(node => node.areEqualsTo(target))) {
      return Some(path ++ List(target))
    }

    adjacentList.filter(n => ! visited.exists(node => n.areEqualsTo(node))).foreach(next => {
      val newVisited: List[LambdaNode] = source :: visited
      val newPath: List[LambdaNode] = path ++ List(next)
      val res = findPath(next, target, newVisited, newPath)
      if(res.isDefined) {
        var unopenedCS: List[LambdaNode] = List()
        var unclosedCS: List[LambdaNode] = List()
        val csOpen = res.head.filter(node => node.nodeType == CallSiteOpenNode)
        val csClose = res.head.filter(node => node.nodeType == CallSiteCloseNode)
        var unvisitedOpenCS = csOpen
        var unvisitedCloseCS = csClose


        // verify if exists cs) nodes without a (cs
        csClose.foreach(_csClose => {
          if (unvisitedOpenCS.exists(node => node.matchCS(_csClose))) {
            val matchedCS = unvisitedOpenCS.filter(node => node.matchCS(_csClose))
            val unmatchedCS = unvisitedOpenCS.filter(node => ! node.matchCS(_csClose))
            if (matchedCS.size > 1) {
              unvisitedOpenCS = unmatchedCS ++ matchedCS.init
            } else {
              unvisitedOpenCS = unmatchedCS
            }
          } else {
            unopenedCS = unopenedCS ++ List(_csClose)
          }
        })

        // verify if exists (cs nodes without a cs)
        csOpen.foreach(_csOpen => {
          if (unvisitedCloseCS.exists(node => node.matchCS(_csOpen))) {
            val matchedCS = unvisitedCloseCS.filter(node => node.matchCS(_csOpen))
            val unmatchedCS = unvisitedCloseCS.filter(node => ! node.matchCS(_csOpen))
            if (matchedCS.size > 1) {
              unvisitedCloseCS = unmatchedCS ++ matchedCS.init
            } else {
              unvisitedCloseCS = unmatchedCS
            }
          } else {
            unclosedCS = unclosedCS ++ List(_csOpen)
          }
        })

        // verify if the unopened and unclosed callsites are not for the same method
        var matchedUnopenedUnclosedCSCalleeMethod: List[(LambdaNode, LambdaNode)] = List()
        unclosedCS.foreach(_csOpen => {
          unopenedCS.filter(node => node.matchCSMethod(_csOpen)).foreach(_csClose => {
            matchedUnopenedUnclosedCSCalleeMethod = matchedUnopenedUnclosedCSCalleeMethod ++ List((_csOpen, _csClose))
          })
        })

        if (unopenedCS.isEmpty || unclosedCS.isEmpty || matchedUnopenedUnclosedCSCalleeMethod.isEmpty) {
          return res
        }
        return None
      }
    })
    return None



      // TODO: remove the code below if the new implementations do not work
//    if(adjacentList.exists(node => node.areEqualsTo(target))) {
//      val currentPath = path ++ List(target)
//      var unopenedCS: List[LambdaNode] = List()
//      var unclosedCS: List[LambdaNode] = List()
//      val csOpen = currentPath.filter(node => node.nodeType == CallSiteOpenNode)
//      val csClose = currentPath.filter(node => node.nodeType == CallSiteCloseNode)
//      var unvisitedOpenCS = csOpen
//      var unvisitedCloseCS = csClose
//
//
//      // verify if exists cs) nodes without a (cs
//      csClose.foreach(_csClose => {
//        if (unvisitedOpenCS.exists(node => node.matchCS(_csClose))) {
//          val matchedCS = unvisitedOpenCS.filter(node => node.matchCS(_csClose))
//          val unmatchedCS = unvisitedOpenCS.filter(node => ! node.matchCS(_csClose))
//          if (matchedCS.size > 1) {
//            unvisitedOpenCS = unmatchedCS ++ matchedCS.init
//          } else {
//            unvisitedOpenCS = unmatchedCS
//          }
//        } else {
//          unopenedCS = unopenedCS ++ List(_csClose)
//        }
//      })
//
//      // verify if exists (cs nodes without a cs)
//      csOpen.foreach(_csOpen => {
//        if (unvisitedCloseCS.exists(node => node.matchCS(_csOpen))) {
//          val matchedCS = unvisitedCloseCS.filter(node => node.matchCS(_csOpen))
//          val unmatchedCS = unvisitedCloseCS.filter(node => ! node.matchCS(_csOpen))
//          if (matchedCS.size > 1) {
//            unvisitedCloseCS = unmatchedCS ++ matchedCS.init
//          } else {
//            unvisitedCloseCS = unmatchedCS
//          }
//        } else {
//          unclosedCS = unclosedCS ++ List(_csOpen)
//        }
//      })
//
//      // verify if the unopened and unclosed callsites are not for the same method
//      var matchedUnopenedUnclosedCSCalleeMethod: List[(LambdaNode, LambdaNode)] = List()
//      unclosedCS.foreach(_csOpen => {
//        unopenedCS.filter(node => node.matchCSMethod(_csOpen)).foreach(_csClose => {
//          matchedUnopenedUnclosedCSCalleeMethod = matchedUnopenedUnclosedCSCalleeMethod ++ List((_csOpen, _csClose))
//        })
//      })
//
//      if (unopenedCS.isEmpty || unclosedCS.isEmpty || matchedUnopenedUnclosedCSCalleeMethod.isEmpty) {
//        return List(path ++ List(target))
//      }
//      return List()
//    }
//
//    var newValidPaths = validPaths
//    adjacentList.filter(n => ! visited.exists(node => n.areEqualsTo(node))).foreach(next => {
//      val newVisited: List[LambdaNode] = source :: visited
//      val newPath: List[LambdaNode] = path ++ List(next)
//      val res = findPath(next, target, newVisited, newPath, validPaths)
//      if(res.nonEmpty) {
//        newValidPaths = newValidPaths ++ res
//      }
//    })
//    return newValidPaths
  }
  def nodes(): scala.collection.Set[LambdaNode] = map.keySet

  def numberOfEdges(): Int = if(map.isEmpty) 0 else  map.values.map(v => v.size).sum
}

// TODO: remove this version for now, this implement different edges and a new graph
//trait LambdaEdge {
//  val source: LambdaNode
//  val target: LambdaNode
//  var edgeType: String
//}
//
//class LambdaGraph() {
//  val nodes = new mutable.HashMap[LambdaNode, Int]()
//  var edges: List[LambdaEdge] = List()
//
//  def addEdge(source: LambdaNode, target: LambdaNode, edge: LambdaEdge): Unit = {
//    if(source == target) {
//      return
//    }
//    if (nodes.contains(source)) {
//      val adjacentEdges = edges.filter(edge => edge.source == source)
//      val commonEdges = adjacentEdges.filter(edge => edge.target == target)
//      if (commonEdges.isEmpty) {
//        if (!nodes.contains(target)) {
//          addNode(target)
//        }
//        edges = edges ++ List(edge)
//      } else if (commonEdges.size == 1) {
//        return
//      } else {
//        removeDuplicatedEdge(edge)
//        return
//      }
//    }
//  }
//
//  def addNode(node: LambdaNode): Unit = {
//    nodes(node) = nodes.size
//  }
//
//  def findPath(source: LambdaNode, target: LambdaNode): Option[List[LambdaNode]] =
//    findPath(source, target, List(), List(source), 0, 0)
//
//  def findPath(source: LambdaNode, target: LambdaNode, visited: List[LambdaNode], path: List[LambdaNode], openCS: Int, closeCS: Int): Option[List[LambdaNode]] = {
//    val adjacentList = edges.filter(edge => edge.source == source).map(edge => edge.target)
//    if(adjacentList.contains(target)) {
//      return Some(path ++ List(target))
//    }
//
//    var newOpenCS = openCS
//    var newCloseCS = closeCS
//
//    if (source.nodeType == CallSiteOpenNode) {
//      newOpenCS = openCS + 1
//    }
//    if (source.nodeType == CallSiteCloseNode) {
//      newCloseCS = closeCS + 1
//    }
//
//    adjacentList.filter(n => ! visited.contains(n)).foreach(next => {
//      val newVisited: List[LambdaNode] = source :: visited
//      val newPath: List[LambdaNode] = path ++ List(next)
//      val res = findPath(next, target, newVisited, newPath, newOpenCS, newCloseCS)
//      if(res.isDefined && newOpenCS == newCloseCS) {
//        return res
//      }
//    })
//    return None
//  }
//  def getNodes(): scala.collection.Set[LambdaNode] = nodes.keySet
//
//  def numberOfEdges(): Int = if(edges.isEmpty) 0 else edges.size
//
//  // TODO: implement this method is necessary
//  def removeDuplicatedEdge(edge: LambdaEdge): Unit = {
//
//  }
//}

// TODO: uncomment this code if the new implementation do not work
//class Graph[NodeT]() {
//
//  val map = new  mutable.HashMap[NodeT,mutable.Set[NodeT]]()
//
//  def addEdge(source: NodeT, target: NodeT): Unit = {
//    if(source == target) {
//      return
//    }
//
//    if(map.contains(source)) {
//      val adjacentList  = map(source)
//      adjacentList += target
//    }
//    else {
//      map(source) = mutable.Set(target)
//    }
//    if(! map.contains(target)) {
//      map(target) = mutable.Set.empty[NodeT]
//    }
//  }
//
//  def findPath(source: NodeT, target: NodeT): Option[List[NodeT]] =
//    findPath(source, target, List(), List(source))
//
//  def findPath(source: NodeT, target: NodeT, visited: List[NodeT], path: List[NodeT]): Option[List[NodeT]] = {
//    val adjacentList = map(source)
//    if(adjacentList.contains(target)) {
//      return Some(path ++ List(target))
//    }
//
//    adjacentList.filter(n => ! visited.contains(n)).foreach(next => {
//      val newVisited: List[NodeT] = source :: visited
//      val newPath: List[NodeT] = path ++ List(next)
//      val res = findPath(next, target, newVisited, newPath)
//      if(res.isDefined) {
//        return res
//      }
//    })
//    return None
//  }
//  def nodes(): scala.collection.Set[NodeT] = map.keySet
//
//  def numberOfEdges(): Int = if(map.isEmpty) 0 else  map.values.map(v => v.size).sum
//}
