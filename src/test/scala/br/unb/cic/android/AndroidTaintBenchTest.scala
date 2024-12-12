package br.unb.cic.android

import br.unb.cic.soot.svfa.jimple.JSVFA
import br.unb.cic.soot.svfa.configuration.AndroidSootConfiguration
import br.unb.cic.soot.svfa.jimple.{FieldSensitive, Interprocedural, JSVFA, PropagateTaint}

import scala.io.Source
import java.util.Properties
import java.io.File
import java.io.FileInputStream

import soot._
import soot.jimple._

import  br.unb.cic.soot.graph._


import java.nio.file.Paths

import br.unb.cic.soot.svfa.configuration.AndroidSootConfiguration

class AndroidTaintBenchTest(apk: String) extends JSVFA
    with RoidSecSpec
    with AndroidSootConfiguration
    with Interprocedural
    with FieldSensitive
    with PropagateTaint
{
  def getApkPath(): String = readProperty("taint-bench") + (s"/$apk.apk")

  def apk(): String = getApkPath()

  def platform(): String = readProperty("sdk") + "/platforms/"

  private def readProperty(key: String) : String = {
    val url = getClass.getResource("/taintbench.properties")
    
    if(url == null) {
      throw new RuntimeException("File taintbench.properties does not exist. Please, make sure to correctly setup this file before executing the test cases.")
    }
    val source = Source.fromURL(url)

    for(s <- source.getLines()) {
      val Array(k, v) = s.split("=")
      if(k == key) {
        return v
      }
    }
    throw new RuntimeException(s"Property $key not set in the file 'taintbench.properties'")
  }

  
  override def analyze(unit: soot.Unit): NodeType = {
    unit match {
      case invokeStmt: InvokeStmt =>
        return analyzeInvokeStmt(invokeStmt.getInvokeExpr)
      case assignStmt: AssignStmt =>
        assignStmt.getRightOp match {
          case invokeStmt: InvokeExpr =>
            return analyzeInvokeStmt(invokeStmt)
          case _ =>
            return SimpleNode
        }
      case _ => return SimpleNode
    }
  }

  def analyzeInvokeStmt(exp: InvokeExpr): NodeType = {
    if (sourceList.contains(exp.getMethod.getSignature)) {
      return SourceNode;
    } else if (sinkList.contains(exp.getMethod.getSignature)) {
      return SinkNode;
    }
    return SimpleNode;
  }
}
