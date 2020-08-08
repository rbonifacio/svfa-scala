package br.unb.cic.soot

import br.unb.cic.soot.graph._
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class InitStringBufferTest extends JSVFATest {
  override def getClassName(): String = "samples.InitStringBufferSample"
  override def getMainMethod(): String = "main"
}