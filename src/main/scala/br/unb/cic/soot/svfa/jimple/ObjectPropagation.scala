package br.unb.cic.soot.svfa.jimple

trait ObjectPropagation {
  def propagateObjectTaint(): Boolean
}

trait PropagateTaint extends ObjectPropagation {
  override def propagateObjectTaint(): Boolean = true
}

trait DontPropagateTaint extends ObjectPropagation {
  override def propagateObjectTaint(): Boolean = false
}
