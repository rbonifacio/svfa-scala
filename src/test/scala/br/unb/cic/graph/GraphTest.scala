package br.unb.cic.graph

import org.scalatest.{BeforeAndAfter, FunSuite}
import br.unb.cic.soot.graph.Graph

class GraphTest extends FunSuite with BeforeAndAfter {
  // TODO: update the tests for graph structure
//  var g: Graph[Int] = _
//
//  before {
//    g  = new Graph()
//    g.addEdge(3, 4)
//
//    g.addEdge(4, 6)
//    g.addEdge(6, 20)
//    g.addEdge(6, 9)
//    g.addEdge(6, 8)
//
//    g.addEdge(4, 3)   // a loop here
//
//    g.addEdge(4, 5)
//    g.addEdge(5, 7)
//    g.addEdge(7, 8)
//
//    g.addEdge(3, 11)
//    g.addEdge(11, 12)
//    g.addEdge(11, 20)
//  }
//  test("After adding three nodes and edges, the graph must have three nodes") {
//    assert(10 == g.nodes().size)
//    assert(12 == g.numberOfEdges())
//  }
//
//  test("It should find valid paths") {
//    assert(Some(List(3, 4, 6, 20)) == g.findPath(3, 20))
//    assert(Some(List(4, 6, 20)) == g.findPath(4, 20))
//    assert(Some(List(3, 4, 6, 9)) == g.findPath(3, 9))
//    assert(Some(List(3, 4, 6, 8)) == g.findPath(3, 8) || Some(List(3, 4, 5, 7, 8)) == g.findPath(3, 8))
//  }
//
//  test("It should report invalid paths") {
//    assert(None == g.findPath(20, 3))
//    assert(None == g.findPath(11, 8))
//  }
//
//  test("A loop should not be an issue") {
//    assert(None != g.findPath(3, 8))
//  }

}
