package br.unb.cic.soot.svfa

import br.unb.cic.soot.graph.NodeType

trait SourceSinkDef {
  def analyze(unit: soot.Unit): NodeType
}
