package br.unb.cic.soot.graph

import scala.collection.mutable

sealed trait NodeType

case object SourceNode extends NodeType { def instance = this }
case object SinkNode extends NodeType { def instance = this }
case object SimpleNode extends NodeType { def instance = this }

case class Node(className: String, method: String, stmt: String, line: Int, nodeType: NodeType)


class Graph[NodeT]() {

  val map = new  mutable.HashMap[NodeT,mutable.MutableList[NodeT]]()

  def addEdge(source: NodeT, target: NodeT): Unit = {
    if(source == target) {
      return
    }

    if(map.contains(source)) {
      val adjacentList  = map.get(source).get
      adjacentList += target
    }
    else {
      map(source) = mutable.MutableList(target)
    }
    if(! map.contains(target)) {
      map(target) = mutable.MutableList.empty[NodeT]
    }
  }

  def findPath(source: NodeT, target: NodeT): Option[List[NodeT]] = {
    return findPath(source, target, List(), List(source))
  }

  def findPath(source: NodeT, target: NodeT, visited: List[NodeT], path: List[NodeT]): Option[List[NodeT]] = {
    val adjacentList = map.get(source).get
    if(adjacentList.contains(target)) {
      return Some(path ++ List(target))
    }

    val nextElements = adjacentList.filter(n => ! visited.contains(n)).foreach(next => {
      val newVisited: List[NodeT] = source :: visited
      val newPath: List[NodeT] = path ++ List(next)
      val res = findPath(next, target, newVisited, newPath)
      if(res != None) {
        return res
      }
    })
    return None
  }
  def nodes() = map.keySet

  def numberOfEdges() = map.values.map(v => v.size).reduce((a, b) => a+b)
}
