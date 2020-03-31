package br.unb.cic.soot.svfa.jimple

trait FieldSensitiveness {
  def isFieldSensitiveAnalysis(): Boolean
}

trait FieldSenstive extends FieldSensitiveness {
  override def isFieldSensitiveAnalysis(): Boolean = true
}

trait FieldInsenstive extends FieldSensitiveness {
  override def isFieldSensitiveAnalysis(): Boolean = false
}
