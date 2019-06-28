package br.unb.cic.soot.svfa

import br.unb.cic.soot.graph.NodeType

trait StmtAnalyzer {
  def analyze(unit: soot.Unit) : NodeType
}
