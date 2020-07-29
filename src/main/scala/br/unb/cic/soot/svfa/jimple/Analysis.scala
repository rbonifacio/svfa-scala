package br.unb.cic.soot.svfa.jimple

trait Analysis {
  def interproceduralAnalysis() : Boolean
  def intraproceduralAnalysis() : Boolean = ! interproceduralAnalysis()
}

trait InterproceduralAnalysis extends Analysis {
  override def interproceduralAnalysis(): Boolean = true
}

trait IntraproceduralAnalysis extends Analysis {
  override def interproceduralAnalysis(): Boolean = false
}
