package br.unb.cic.soot.svfa.jimple

trait AnalysisDepth {
  def maxDepth(): Int = 0
  def isLimited(): Boolean= maxDepth() > 0
}

trait StandardLimitedAnalysis	extends AnalysisDepth {
  override def maxDepth(): Int = 10
}
