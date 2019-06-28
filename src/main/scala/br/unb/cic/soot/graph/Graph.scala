package br.unb.cic.soot.graph

import scala.collection.mutable

abstract class NodeType

case class SourceNode() extends NodeType
case class SinkNode() extends NodeType
case class SimpleNode() extends NodeType

case class Node(className: String, method: String, stmt: String, line: Int, nodeType: NodeType)


class Graph() {

  val map = new  mutable.HashMap[Node,(Int, mutable.MutableList[Node])]()

  def addEdge(source: Node, target: Node): Unit = {
    if(map.contains(source)) {
      val (_, adjacent) = map.get(source).get
      adjacent += target
    }
    else {
      val id = map.size + 1
      map(source) = (id, mutable.MutableList(target))
    }
  }

  def nodes() = map.keySet
}
