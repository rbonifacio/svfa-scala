package br.unb.cic.dsl

import br.unb.cic.soot.JSVFATest
import br.unb.cic.soot.graph.{NodeType, SimpleNode}
import org.scalatest.FunSuite
import br.unb.cic.soot.svfa.jimple.dsl.LanguageParser

class ParserTestSuite extends FunSuite {
    var simpleCode =
        """
        rule skipNativeClasses =
            if NativeRule() then DoNothing()
        """


    var definitionsCode =
        """
        rule skipNativeClasses =
            if NativeRule then DoNothing

        tokens
        id: [a-zA-Z][a-zA-Z0-9]*
        literal: \"[a-zA-Z0-9\.<>:]*\"
        int: [1-9][0-9]*
        rule
        if
        then
        =
        (
        )
        :
        ,

        structure
        method: id ( [mappedValue]* )
        action: id ( [mappedValue]* )
        mappedValue: id : [literal | int]
        if: if [method] then [action]
        rule: rule id = [if]
        """

    var code =
//        """
//        rule stringBuilderAppend =
//          if NamedMethodRule(className: "java.lang.StringBuilder", methodName: "append")
//            then [
//              CopyFromMethodArgumentToBaseObject(from: 0),
//              CopyFromMethodArgumentToLocal(from: 0),
//              CopyFromMethodCallToLocal()
//            ]
//        """
        """
        rule skipNativeMethods = if NativeRule() then DoNothing()

        rule skipMethodsWithoutActiveBody =
            if MissingActiveBodyRule() then DoNothing()

        rule stringBuffer =
            if NamedMethodRule(className: "java.lang.StringBuffer", methodName: "toString") then DoNothing()
        """

    var defaultRules =
    """
    rule arrayCopy =
      if NamedMethodRule(className: "java.lang.System", methodName: "arraycopy")
        then CopyBetweenArgs(from: 0, target: 2)

    rule stringBuilderToString =
      if NamedMethodRule(className: "java.lang.StringBuilder", methodName: "toString")
        then CopyFromMethodCallToLocal()

    rule stringBuilderAppend =
      if NamedMethodRule(className: "java.lang.StringBuilder", methodName: "append")
        then [
          CopyFromMethodArgumentToBaseObject(from: 0),
          CopyFromMethodArgumentToLocal(from: 0),
          CopyFromMethodCallToLocal()
        ]

    rule stringBufferAppend =
      if NamedMethodRule(className: "java.lang.StringBuffer", methodName: "append")
        then CopyFromMethodArgumentToBaseObject(from: 0)

    rule stringBufferInit =
      if NamedMethodRule(className: "java.lang.StringBuffer", methodName: "<init>")
        then CopyFromMethodArgumentToBaseObject(from: 0)

    rule fileInit =
      if NamedMethodRule(className: "java.io.File", methodName: "<init>")
        then CopyFromMethodArgumentToBaseObject(from: 0)

    rule entrySetOfMap =
      if NamedMethodRule(className: "java.util.Map", methodName: "entrySet")
        then CopyFromMethodCallToLocal()

    rule getKeyOfMapEntry =
      if NamedMethodRule(className: "java.util.Map$Entry", methodName: "getKey")
        then CopyFromMethodCallToLocal()

    rule getValueOfMapEntry =
      if NamedMethodRule(className: "java.util.Map$Entry", methodName: "getValue")
        then CopyFromMethodCallToLocal()

    rule iteratorOfSet =
      if NamedMethodRule(className: "java.util.Set", methodName: "iterator")
        then CopyFromMethodCallToLocal()

    rule iteratorNext =
      if NamedMethodRule(className: "java.util.Iterator", methodName: "next")
        then CopyFromMethodCallToLocal()

    rule nextElementOfEnumeration =
      if NamedMethodRule(className: "java.util.Enumeration", methodName: "nextElement")
        then CopyFromMethodCallToLocal()

    rule stringBufferToString =
      if NamedMethodRule(className: "java.lang.StringBuffer", methodName: "toString")
        then CopyFromMethodCallToLocal()

    rule skipNativeMethods = if NativeRule() then DoNothing()

    rule skipMethodsWithoutActiveBody =
      if MissingActiveBodyRule() then DoNothing()
    """

    test("Evaluate code") {
        val parser = new LanguageParser(new JSVFATest {
            override def getClassName(): String = ""

            override def getMainMethod(): String = ""

            override def analyze(unit: soot.Unit): NodeType = SimpleNode
        })

        val tokens = parser.apply(code)

        var stringOfTokens = ""
        tokens.foreach(token => stringOfTokens = stringOfTokens + token)

        println(stringOfTokens)

        val rules = parser.evaluate(code)

        println(rules.map(rule => rule.toString()).reduce((acc, curr) => acc + "\n" + curr))

        assert(rules.length == 3)
    }

    test("Default Rules") {
        val parser = new LanguageParser(new JSVFATest {
            override def getClassName(): String = ""

            override def getMainMethod(): String = ""

            override def analyze(unit: soot.Unit): NodeType = SimpleNode
        })

        val tokens = parser.apply(defaultRules)

        var stringOfTokens = ""
        tokens.foreach(token => stringOfTokens = stringOfTokens + token)

        println(stringOfTokens)

        val rules = parser.evaluate(defaultRules)

        println(rules.map(rule => rule.toString()).reduce((acc, curr) => acc + "\n" + curr))

        assert(rules.length == 15)
    }
}
