package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import br.unb.cic.soot.svfa.jimple.{FieldSenstive, InterproceduralAnalysis, JSVFA}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}
import soot.{Scene, SootMethod}

abstract class JSVFATest extends JSVFA with InterproceduralAnalysis with FieldSenstive {
  def getClassName(): String
  def getMainMethod(): String

  override def sootClassPath(): String = ""

  override def applicationClassPath(): List[String] = List("target/scala-2.12/test-classes", "/Users/rbonifacio/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar")

  override def getEntryPoints(): List[SootMethod] = {
    val sootClass = Scene.v().getSootClass(getClassName())
    List(sootClass.getMethodByName(getMainMethod()))
  }

  override def getIncludeList(): List[String] = List(
      "java.lang.*",
      "java.util.*"
    )

  override def analyze(unit: soot.Unit): NodeType = {
    if(unit.isInstanceOf[InvokeStmt]) {
      val invokeStmt = unit.asInstanceOf[InvokeStmt]
      return analyzeInvokeStmt(invokeStmt.getInvokeExpr)
    }
    if(unit.isInstanceOf[soot.jimple.AssignStmt]) {
      val assignStmt = unit.asInstanceOf[AssignStmt]
      if(assignStmt.getRightOp.isInstanceOf[InvokeExpr]) {
        val invokeStmt = assignStmt.getRightOp.asInstanceOf[InvokeExpr]
        return analyzeInvokeStmt(invokeStmt)
      }
    }
    return SimpleNode
  }

  def analyzeInvokeStmt(exp: InvokeExpr) : NodeType =
    exp.getMethod.getName match {
      case "source" => SourceNode
      case "sink"   => SinkNode
      case _        => SimpleNode
    }
}
