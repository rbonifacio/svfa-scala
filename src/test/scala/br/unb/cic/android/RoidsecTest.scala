package br.unb.cic.android

import br.unb.cic.soot.graph.{NodeType, _}
import org.scalatest.FunSuite
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}


class RoidsecTest extends FunSuite {
  test("in the class Basic2 we should detect 6 flows") {
    val svfa = new AndroidTaintBenchTest("roidsec")
    svfa.buildSparseValueFlowGraph()
    // println(svfa.svgToDotModel())
    assert(svfa.reportConflictsSVG().size == 1)
  }
}
