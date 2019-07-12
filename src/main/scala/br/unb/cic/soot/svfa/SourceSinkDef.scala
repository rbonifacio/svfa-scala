package br.unb.cic.soot.svfa

import br.unb.cic.soot.graph.NodeType

trait SourceSinkDef {
  this : SVFA =>
  def analyze(unit: soot.Unit) : NodeType
}
