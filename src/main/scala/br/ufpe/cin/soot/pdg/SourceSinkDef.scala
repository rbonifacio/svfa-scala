package br.ufpe.cin.soot.pdg

import br.ufpe.cin.soot.graph.NodeType

trait SourceSinkDef {
  this : PDG =>
  def analyze(unit: soot.Unit) : NodeType
}
