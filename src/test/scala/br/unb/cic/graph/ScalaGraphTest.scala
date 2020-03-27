package br.unb.cic.graph

import org.scalatest.FunSuite
import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._


abstract class NType
case class Source() extends NType
case class Sink() extends NType
case class Simple() extends NType

case class Method(c: String, m: String, t: NType)

/**
 * A test suite for helping me to get used
 * to the scala Graph library =)
 *
 * @author rbonifacio
 */
class ScalaGraphTest extends FunSuite {

  test("simple graph") {
    val g = Graph(1~2, 2~3, 3~4)

    val path = g.nodes.find(2).get pathTo g.nodes.find(4).get

    assert(4 == g.nodes.size)
    assert(path != None)
  }

  test("Testing pathUntil method using a case class") {
    val m1 = Method("Foo", "m", Source())
    val m2 = Method("Blah", "n", Sink())
    val m3 = Method("Blah", "abc", Simple())

    val g = Graph(m1 ~ m2, m3)

    assert(3 == g.nodes.size)
    assert(1 == g.edges.size)

    val sourceNodes = g.nodes.filter((n : Method) => n.t == Source())
    val sinkNodes = g.nodes.filter((n : Method) => n.t == Sink())

    val n1 = g.find(m1).get
    val n2 = g.find(m2).get
    val n3 = g.find(m3).get

    val p1 = n1 pathTo n2
    val p2 = n1 pathTo n3

    assert(p1 != None)
    assert(p2 == None)

  }

}
