package br.unb.cic.soot

import br.unb.cic.soot.graph._
import org.scalatest.{BeforeAndAfter, FunSuite}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class BlackBoardTest extends JSVFATest {
  override def getClassName(): String = "samples.BlackBoard"
  override def getMainMethod(): String = "main"
}

