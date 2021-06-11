package br.unb.cic.soot

import br.unb.cic.soot.graph.{NodeType, SimpleNode, SinkNode, SourceNode}
import soot.jimple.{AssignStmt, InvokeExpr, InvokeStmt}

class TwoIfElseTest(leftchangedlines: Array[Int] , rightchangedlines: Array[Int]) extends JSVFATest {
    override def getClassName(): String = "samples.TwoIfElse"
    override def getMainMethod(): String = "main"

    def this(){
        this(Array.empty[Int], Array.empty[Int])
    }

    override def analyze(unit: soot.Unit): NodeType = {

        if (!leftchangedlines.isEmpty && !rightchangedlines.isEmpty){
            if (leftchangedlines.contains(unit.getJavaSourceStartLineNumber)){
                return SourceNode
            } else if (rightchangedlines.contains(unit.getJavaSourceStartLineNumber)){
                return SinkNode
            }
        }

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