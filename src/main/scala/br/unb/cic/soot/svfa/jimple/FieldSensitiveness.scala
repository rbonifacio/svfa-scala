package br.unb.cic.soot.svfa.jimple

trait FieldSensitiveness {
  def isFieldSensitiveAnalysis(): Boolean
}

trait FieldSensitive extends FieldSensitiveness {
  override def isFieldSensitiveAnalysis(): Boolean = true
}

trait FieldInsensitive extends FieldSensitiveness {
  override def isFieldSensitiveAnalysis(): Boolean = false
}
