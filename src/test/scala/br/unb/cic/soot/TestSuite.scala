package br.unb.cic.soot

import br.unb.cic.soot.graph.Graph
import br.unb.cic.soot.graph.LambdaNode
import org.scalatest.{BeforeAndAfter, FunSuite}
import scala.collection.mutable.ListBuffer

class TestSuite extends FunSuite with BeforeAndAfter {

  test("we should correctly compute the number of nodes and edges in the BlackBoardTest sample") {
//    val controlDependence = new BlackBoardTest(Array (14, 12), Array (4, 7, 17))
    val controlDependence = new BlackBoardTest()
    controlDependence.buildSparseValueFlowGraph()
    var x = controlDependence.findConflictingPaths()
    println("Conflits: "+controlDependence.findConflictingPaths().size);
    println(controlDependence.svgToDotModel())

    //    var source = ListBuffer[LambdaNode]()
    //    var sink = ListBuffer[LambdaNode]()
    //
    //    for(n <- svfa.svg.nodes) {
    //      if (n.nodeType.equals(SourceNode)) {
    //        source += n
    //      }else if (n.nodeType.equals(SinkNode)){
    //        sink += n
    //      }
    //    }
    //
    //    if (!thereIsPath(source, sink, svfa.svg) && !thereIsPath(sink, source, svfa.svg)){
    //      println("There is no path!")
    //    }

    //    assert(svfa.svg.numberOfNodes() == 10)
    //    assert(svfa.svg.numberOfEdges() == 11)
  }

  def thereIsPath(source: ListBuffer[LambdaNode], target: ListBuffer[LambdaNode], graph: Graph): Boolean ={
    var thereIs = false
    source.foreach(s =>{
      target.foreach(t =>{
        val path = graph.findPath(s, t)
        if (path.size>0) {
          println("There is a path from \""+s.show()+"\" to \""+ t.show()+"\"")
          thereIs = true
        }
      })
    })
    return thereIs
  }

  test("we should correctly compute the number of nodes and edges in the NestedAll1Test sample") {
    val controlDependence = new NestedAll1Test()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 18)
    assert(controlDependence.svg.numberOfEdges() == 17)
  }

  test("we should correctly compute the number of nodes and edges in the NestedAll2Test sample") {
    val controlDependence = new NestedAll2Test()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 18)
    assert(controlDependence.svg.numberOfEdges() == 17)
  }

  test("we should correctly compute the number of nodes and edges in the NestedIfTest sample") {
    val controlDependence = new NestedIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 8)
    assert(controlDependence.svg.numberOfEdges() == 7)
  }

  test("we should correctly compute the number of nodes and edges in the NestedIfElseIfTest sample") {
    val controlDependence = new NestedIfElseIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 16)
    assert(controlDependence.svg.numberOfEdges() == 15)
  }

  test("we should correctly compute the number of nodes and edges in the NestedIfWhileIfElseTest sample") {
    val controlDependence = new NestedIfWhileIfElseTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 10)
    assert(controlDependence.svg.numberOfEdges() == 9)
  }

  test("we should correctly compute the number of nodes and edges in the NestedThreeIfTest sample") {
    val controlDependence = new NestedThreeIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 12)
    assert(controlDependence.svg.numberOfEdges() == 11)
  }

  test("we should correctly compute the number of nodes and edges in the NestedThreeWhileTest sample") {
    val controlDependence = new NestedThreeWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 9)
    assert(controlDependence.svg.numberOfEdges() == 8)
  }

  test("we should correctly compute the number of nodes and edges in the NestedTwoWhileIfTest sample") {
    val controlDependence = new NestedTwoWhileIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 10)
    assert(controlDependence.svg.numberOfEdges() == 9)
  }

  test("we should correctly compute the number of nodes and edges in the NestedWhileAndIfTest sample") {
    val controlDependence = new NestedWhileAndIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 12)
    assert(controlDependence.svg.numberOfEdges() == 11)
  }

  test("we should correctly compute the number of nodes and edges in the NestedWhileDoWhileIfTest sample") {
    val controlDependence = new NestedWhileDoWhileIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 11)
    assert(controlDependence.svg.numberOfEdges() == 13)
  }

  test("we should correctly compute the number of nodes and edges in the NestedWhileIfWhileTest sample") {
    val controlDependence = new NestedWhileIfWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 9)
    assert(controlDependence.svg.numberOfEdges() == 8)
  }

  test("we should correctly compute the number of nodes and edges in the NestedWhileWhileIfWhileTest sample") {
    val controlDependence = new NestedWhileWhileIfWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 14)
    assert(controlDependence.svg.numberOfEdges() == 13)
  }

  test("we should correctly compute the number of nodes and edges in the OneDoWhileAndOneIfElseTest sample") {
    val controlDependence = new OneDoWhileAndOneIfElseTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 10)
    assert(controlDependence.svg.numberOfEdges() == 12)
  }

  test("we should correctly compute the number of nodes and edges in the OneDoWhileAndOneIfTest sample") {
    val controlDependence = new OneDoWhileAndOneIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 9)
    assert(controlDependence.svg.numberOfEdges() == 11)
  }

  test("we should correctly compute the number of nodes and edges in the OneDoWhileAndWhileTest sample") {
    val controlDependence = new OneDoWhileAndWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 9)
    assert(controlDependence.svg.numberOfEdges() == 11)
  }

  test("we should correctly compute the number of nodes and edges in the OneDoWhileTest sample") {
    val controlDependence = new OneDoWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 6)
    assert(controlDependence.svg.numberOfEdges() == 6)
  }

  test("we should correctly compute the number of nodes and edges in the OneForTest sample") {
    val controlDependence = new OneForTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 8)
    assert(controlDependence.svg.numberOfEdges() == 7)
  }

  test("we should correctly compute the number of nodes and edges in the OneIfElseAndOneWhileTest sample") {
    val controlDependence = new OneIfElseAndOneWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 10)
    assert(controlDependence.svg.numberOfEdges() == 9)
  }

  test("we should correctly compute the number of nodes and edges in the OneIfElseTest sample") {
    val controlDependence = new OneIfElseTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 7)
    assert(controlDependence.svg.numberOfEdges() == 6)
  }

  test("we should correctly compute the number of nodes and edges in the OneIfTest sample") {
    val controlDependence = new OneIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 6)
    assert(controlDependence.svg.numberOfEdges() == 5)
  }

  test("we should correctly compute the number of nodes and edges in the OneWhileAndNestedIfElseTest sample") {
    val controlDependence = new OneWhileAndNestedIfElseTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 10)
    assert(controlDependence.svg.numberOfEdges() == 9)
  }

  test("we should correctly compute the number of nodes and edges in the OneWhileAndNestedIfTest sample") {
    val controlDependence = new OneWhileAndNestedIfTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 9)
    assert(controlDependence.svg.numberOfEdges() == 8)
  }

  test("we should correctly compute the number of nodes and edges in the OneWhileAndOneIfElseTest sample") {
    val controlDependence = new OneWhileAndOneIfElseTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 10)
    assert(controlDependence.svg.numberOfEdges() == 9)
  }

  test("we should correctly compute the number of nodes and edges in the OneWhileTest sample") {
    val controlDependence = new OneWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 6)
    assert(controlDependence.svg.numberOfEdges() == 5)
  }

  test("we should correctly compute the number of nodes and edges in the TwoIfElseTest sample") {
    val controlDependence = new TwoIfElseTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 11)
    assert(controlDependence.svg.numberOfEdges() == 10)
  }

  test("we should correctly compute the number of nodes and edges in the WhileIfIfElseDoWhileTest sample") {
    val controlDependence = new WhileIfIfElseDoWhileTest()
    controlDependence.buildSparseValueFlowGraph()
//    println(controlDependence.svgToDotModel())
    assert(controlDependence.svg.numberOfNodes() == 16)
    assert(controlDependence.svg.numberOfEdges() == 16)
  }
}
