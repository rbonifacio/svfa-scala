package br.unb.cic.soot.graph

import scala.collection.mutable

sealed trait NodeType

case object SourceNode extends NodeType { def instance: SourceNode.type = this }
case object SinkNode extends NodeType { def instance: SinkNode.type = this }
case object SimpleNode extends NodeType { def instance: SimpleNode.type = this }

case class Node(className: String, method: String, stmt: String, line: Int, nodeType: NodeType)


class Graph[NodeT]() {

  val map = new  mutable.HashMap[NodeT,mutable.Set[NodeT]]()

  def addEdge(source: NodeT, target: NodeT): Unit = {
    if(source == target) {
      return
    }

    if(map.contains(source)) {
      val adjacentList  = map(source)
      adjacentList += target
    }
    else {
      map(source) = mutable.Set(target)
    }
    if(! map.contains(target)) {
      map(target) = mutable.Set.empty[NodeT]
    }
  }

  def findPath(source: NodeT, target: NodeT): Option[List[NodeT]] =
    findPath(source, target, List(), List(source))


  def findPath(source: NodeT, target: NodeT, visited: List[NodeT], path: List[NodeT]): Option[List[NodeT]] = {
    val adjacentList = map(source)
    if(adjacentList.contains(target)) {
      return Some(path ++ List(target))
    }

    adjacentList.filter(n => ! visited.contains(n)).foreach(next => {
      val newVisited: List[NodeT] = source :: visited
      val newPath: List[NodeT] = path ++ List(next)
      val res = findPath(next, target, newVisited, newPath)
      if(res.isDefined) {
        return res
      }
    })
    return None
  }
  def nodes(): scala.collection.Set[NodeT] = map.keySet

  def numberOfEdges(): Int = if(map.isEmpty) 0 else  map.values.map(v => v.size).sum
}
