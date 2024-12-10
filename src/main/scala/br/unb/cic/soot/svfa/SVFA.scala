package br.unb.cic.soot.svfa

import soot._
import br.unb.cic.soot.svfa.configuration.SootConfiguration

/** Base class for all implementations of SVFA algorithms.
  */
abstract class SVFA extends SootConfiguration {

  var svg = new br.unb.cic.soot.graph.Graph()

  def buildSparseValueFlowGraph() {
    configureSoot()
    beforeGraphConstruction()
    val (pack, t) = createSceneTransform()
    PackManager.v().getPack(pack).add(t)
    configurePackages().foreach(p => PackManager.v().getPack(p).apply())
    afterGraphConstruction()
  }

  def svgToDotModel(): String = {
    svg.toDotModel()
  }

  def reportConflictsSVG() = {
    svg.reportConflicts()
  }

}
