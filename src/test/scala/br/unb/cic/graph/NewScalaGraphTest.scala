package br.unb.cic.graph

import br.unb.cic.soot.graph.{SimpleNode, SinkNode, SourceNode, Stmt, StmtNode}
import org.scalatest.FunSuite

class NewScalaGraphTest extends FunSuite {

  test("simple graph") {
    val g = new br.unb.cic.soot.graph.Graph()

    val FakeSouce = StmtNode(Stmt("FooClass", "FooMethod", "FooStmt", 1), SourceNode)
    val FakeSink = StmtNode(Stmt("BarClass", "BarMethod", "BarStmt", 2), SinkNode)

    g.addEdge(FakeSouce, FakeSink)

    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)
  }

  test("try add duplicate node") {
    val g = new br.unb.cic.soot.graph.Graph()

    val FakeSouce = StmtNode(Stmt("FooClass", "FooMethod", "FooStmt", 1), SourceNode)
    val FakeSouceCopy = StmtNode(Stmt("FooClass", "FooMethod", "FooStmt", 1), SourceNode)

    g.addNode(FakeSouce)
    assert(g.numberOfNodes() == 1)
    g.addNode(FakeSouce)
    assert(g.numberOfNodes() == 1)
    g.addNode(FakeSouceCopy)

    assert(g.numberOfNodes() == 1)
    assert(g.numberOfEdges() == 0)
  }

  test("try add duplicate edges") {
    val g = new br.unb.cic.soot.graph.Graph()

    val FakeSouce = StmtNode(Stmt("FooClass", "FooMethod", "FooStmt", 1), SourceNode)
    val FakeSouceCopy = StmtNode(Stmt("FooClass", "FooMethod", "FooStmt", 1), SourceNode)
    val FakeSink = StmtNode(Stmt("BarClass", "BarMethod", "BarStmt", 2), SinkNode)
    val FakeSinkCopy = StmtNode(Stmt("BarClass", "BarMethod", "BarStmt", 2), SinkNode)

    g.addEdge(FakeSouce, FakeSink)
    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)
    g.addEdge(FakeSouce, FakeSink)
    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)
    g.addEdge(FakeSouceCopy, FakeSinkCopy)
    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)
    g.addEdge(FakeSouce, FakeSinkCopy)
    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)
    g.addEdge(FakeSouceCopy, FakeSink)

    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)
  }

  test("try find all paths") {
    val g = new br.unb.cic.soot.graph.Graph()

    val FakeSource = StmtNode(Stmt("FooClass", "FooMethod", "FooStmt", 1), SourceNode)
    val NormalStmt = StmtNode(Stmt("NormalClass", "NormalMethod", "NormalStmt", 3), SimpleNode)
    val FakeSink = StmtNode(Stmt("BarClass", "BarMethod", "BarStmt", 2), SinkNode)
    val FakeSink2 = StmtNode(Stmt("BooClass", "BooMethod", "BooStmt", 2), SinkNode)

    g.addEdge(FakeSource, NormalStmt)
    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)

    g.addEdge(NormalStmt, FakeSink)
    assert(g.numberOfNodes() == 3)
    assert(g.numberOfEdges() == 2)

    g.addEdge(NormalStmt, FakeSink2)
    assert(g.numberOfNodes() == 4)
    assert(g.numberOfEdges() == 3)

    g.addEdge(FakeSource, FakeSink2)
    assert(g.numberOfNodes() == 4)
    assert(g.numberOfEdges() == 4)

    assert(g.findPath(FakeSource, FakeSink).nonEmpty)
    assert(g.findPath(FakeSource, FakeSink2).nonEmpty)
  }

  ignore("base") {
    val g = new br.unb.cic.soot.graph.Graph()

    val FakeSouce = StmtNode(Stmt("FooClass", "FooMethod", "FooStmt", 1), SourceNode)
    val FakeSink = StmtNode(Stmt("BarClass", "BarMethod", "BarStmt", 2), SinkNode)

    g.addEdge(FakeSouce, FakeSink)

    val path = g.findPath(FakeSouce, FakeSink)

    assert(g.numberOfNodes() == 2)
    assert(g.numberOfEdges() == 1)
    //    assert(path != None)
  }

//  test("Testing pathUntil method using a case class") {
//    val m1 = Method("Foo", "m", Source())
//    val m2 = Method("Blah", "n", Sink())
//    val m3 = Method("Blah", "abc", Simple())
//
//    val g = Graph(m1 ~ m2, m3)
//
//    assert(3 == g.nodes.size)
//    assert(1 == g.edges.size)
//
//    val sourceNodes = g.nodes.filter((n: Method) => n.t == Source())
//    val sinkNodes = g.nodes.filter((n: Method) => n.t == Sink())
//
//    val n1 = g.find(m1).get
//    val n2 = g.find(m2).get
//    val n3 = g.find(m3).get
//
//    val p1 = n1 pathTo n2
//    val p2 = n1 pathTo n3
//
//    assert(p1 != None)
//    assert(p2 == None)
//  }
}
