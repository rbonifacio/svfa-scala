package br.unb.cic.soot.svfa.jimple.dsl

import br.unb.cic.soot.svfa.jimple.JSVFA

import scala.util.parsing.combinator._
import scala.util.matching.Regex
import br.unb.cic.soot.svfa.jimple.rules.MethodRule

import scala.collection.mutable.HashMap

// Tokens
sealed trait Token
case class tId(value: String) extends Token
case class tLiteral(value: String) extends Token
case class tNumber(value: Int) extends Token
case class tRule() extends Token
case class tIf() extends Token
case class tThen() extends Token
case class tEq() extends Token
case class tOpenRoundBrackets() extends Token
case class tCloseRoundBrackets() extends Token
case class tOpenSquareBrackets() extends Token
case class tCloseSquareBrackets() extends Token
case class tColon() extends Token
case class tComma() extends Token

// AST
sealed trait AST
case class tMappedString(id: tId, value: tLiteral) extends Token
case class tMappedNumber(id: tId, value: tNumber) extends Token
case class tMethod(id: tId, params: List[tMappedString]) extends Token
case class tAction(id: tId, definitions: List[tMappedNumber]) extends Token
case class tIfThen(condition: tMethod, thenBlock: List[tAction]) extends Token
case class tRuleComplete(id: tId, conditional: tIfThen) extends Token

class LanguageParser(val jsvfa: JSVFA) extends RegexParsers {
  val factory = new RuleFactory(jsvfa)

  override def skipWhitespace: Boolean = true

  override protected val whiteSpace: Regex = """[ \t\r\f\n]+""".r

  // tokens
  def ID: Parser[tId] =
    """[a-zA-Z][a-zA-Z0-9_.<>$]*""".r ^^ { case v => tId(v.toString) }

  def LITERAL: Parser[tLiteral] =
    """"[^"]*"""".r ^^ { v =>
      val value = v.substring(1, v.length - 1)
      tLiteral(value)
    }

  def NUMBER: Parser[tNumber] =
    """(0|[1-9][0-9]*)""".r ^^ { case v => tNumber(v.toInt) }

  def RULE: Parser[tRule] = """rule""".r ^^ { case _ => tRule() }

  def IF: Parser[tIf] = """if""".r ^^ { case _ => tIf() }

  def THEN: Parser[tThen] = """then""".r ^^ { case _ => tThen() }

  def EQ: Parser[tEq] = """=""".r ^^ { case _ => tEq() }

  def OPEN_ROUND_BRACKETS: Parser[tOpenRoundBrackets] = """\(""".r ^^ {
    case _ => tOpenRoundBrackets()
  }

  def CLOSE_ROUND_BRACKETS: Parser[tCloseRoundBrackets] = """\)""".r ^^ {
    case _ => tCloseRoundBrackets()
  }

  def OPEN_SQUARE_BRACKETS: Parser[tOpenSquareBrackets] = """\[""".r ^^ {
    case _ => tOpenSquareBrackets()
  }

  def CLOSE_SQUARE_BRACKETS: Parser[tCloseSquareBrackets] = """\]""".r ^^ {
    case _ => tCloseSquareBrackets()
  }

  def COLON: Parser[tColon] = """:""".r ^^ { case _ => tColon() }

  def COMMA: Parser[tComma] = """,""".r ^^ { case _ => tComma() }

  def MAPPED_STRING: Parser[tMappedString] =
    ID ~ COLON ~ LITERAL ^^ { case id ~ _ ~ value => tMappedString(id, value) }

  def MAPPED_STRINGS: Parser[List[tMappedString]] =
    MAPPED_STRING ~ ((COMMA ~ MAPPED_STRING).+).? ^^ { case head ~ tail =>
      tail match {
        case Some(value) => List(head) ++ value.map(reg => reg._2)
        case None        => List(head)
      }
    }

  def MAPPED_NUMBER: Parser[tMappedNumber] =
    ID ~ COLON ~ NUMBER ^^ { case id ~ _ ~ value => tMappedNumber(id, value) }

  def MAPPED_NUMBERS: Parser[List[tMappedNumber]] =
    MAPPED_NUMBER ~ ((COMMA ~ MAPPED_NUMBER).+).? ^^ { case head ~ tail =>
      tail match {
        case Some(value) => List(head) ++ value.map(reg => reg._2)
        case None        => List(head)
      }
    }

  // Methods
  def NATIVE_RULE: Parser[String] =
    """NativeRule""".r ^^ {
      _.toString
    }

  def MISSING_ACTIVE_BODY_RULE: Parser[String] =
    """MissingActiveBodyRule""".r ^^ {
      _.toString
    }

  def NAMED_METHOD_RULE: Parser[String] =
    """NamedMethodRule""".r ^^ {
      _.toString
    }

  def METHODS: Parser[String] =
    NATIVE_RULE | MISSING_ACTIVE_BODY_RULE | NAMED_METHOD_RULE ^^ {
      _.toString
    }

  def METHOD: Parser[tMethod] =
    METHODS ~ OPEN_ROUND_BRACKETS ~ MAPPED_STRINGS.? ~ CLOSE_ROUND_BRACKETS ^^ {
      case method ~ _ ~ params ~ _ =>
        tMethod(tId(method), params.getOrElse(List()))
    }

  // Actions
  def DO_NOTHING: Parser[String] =
    """DoNothing""".r ^^ {
      _.toString
    }

  def COPY_BETWEEN_ARGS: Parser[String] =
    """CopyBetweenArgs""".r ^^ {
      _.toString
    }

  def COPY_FROM_METHOD_ARGUMENT_TO_BASE_OBJECT: Parser[String] =
    """CopyFromMethodArgumentToBaseObject""".r ^^ {
      _.toString
    }

  def COPY_FROM_METHOD_ARGUMENT_TO_LOCAL: Parser[String] =
    """CopyFromMethodArgumentToLocal""".r ^^ {
      _.toString
    }

  def COPY_FROM_METHOD_CALL_TO_LOCAL: Parser[String] =
    """CopyFromMethodCallToLocal""".r ^^ {
      _.toString
    }

  def ACTIONS: Parser[String] =
    DO_NOTHING | COPY_BETWEEN_ARGS |
      COPY_FROM_METHOD_ARGUMENT_TO_BASE_OBJECT | COPY_FROM_METHOD_CALL_TO_LOCAL |
      COPY_FROM_METHOD_ARGUMENT_TO_LOCAL ^^ {
        _.toString
      }

  def ACTION: Parser[tAction] =
    ACTIONS ~ OPEN_ROUND_BRACKETS ~ MAPPED_NUMBERS.? ~ CLOSE_ROUND_BRACKETS ^^ {
      case action ~ _ ~ definitions ~ _ =>
        tAction(tId(action), definitions.getOrElse(List()))
    }

  def ACTION_LIST: Parser[List[tAction]] =
    OPEN_SQUARE_BRACKETS.? ~ ACTION ~ ((COMMA ~ ACTION).+).? ~ CLOSE_SQUARE_BRACKETS.? ^^ {
      case openBrackets ~ head ~ tail ~ closeBrackets =>
        tail match {
          case Some(value) =>
//        if (openBrackets.isDefined && closeBrackets.isDefined) {
            List(head) ++ value.map(reg => reg._2)
//        } else {
//          List()
//        }
          case None => List(head)
        }
    }

  def IF_THEN: Parser[tIfThen] =
    IF ~ METHOD ~ THEN ~ ACTION_LIST ^^ { case _ ~ method ~ _ ~ actions =>
      tIfThen(method, actions)
    }

  def RULE_COMPLETE: Parser[tRuleComplete] =
    RULE ~ ID ~ EQ ~ IF_THEN ^^ { case _ ~ id ~ _ ~ ifThen =>
      tRuleComplete(id, ifThen)
    }

  def rule: Parser[MethodRule] =
    RULE_COMPLETE ^^ {
      case rule => {
        val method = rule.conditional.condition.id.value
        val actions = rule.conditional.thenBlock
        val args = HashMap.empty[String, String]
        var defs = HashMap.empty[String, HashMap[String, Int]]
        rule.conditional.condition.params.foreach((mappedValue) =>
          args(mappedValue.id.value) = mappedValue.value.value
        )

        actions.foreach(action => {
          val actionDefs = HashMap.empty[String, Int]
          action.definitions.foreach((mappedValue) =>
            actionDefs(mappedValue.id.value) = mappedValue.value.value
          )

          defs(action.id.value) = actionDefs
        })

        factory.create(
          method,
          actions.map(action => action.id.value),
          args,
          defs
        )
      }
    }

  def getTokens: Parser[List[String]] = {
    phrase(rep1(RULE_COMPLETE | IF_THEN | METHOD | ACTION | ID)) ^^ { tokens =>
      tokens.map(token => token.toString)
    }
  }

  def getAST: Parser[List[MethodRule]] = {
    phrase(rep1(rule)) ^^ { statements => statements }
  }

  def apply(code: String): List[String] = {
    parse(getTokens, code) match {
      case Success(result, _) => result
      case Failure(msg, _)    => println(s"FAILURE: $msg"); List.empty[String]
      case Error(msg, _)      => println(s"ERROR: $msg"); List.empty[String]
    }
  }

  def evaluate(code: String): List[MethodRule] = {
    parse(getAST, code) match {
      case Success(result, _) => result
      case Failure(msg, _) => println(s"FAILURE: $msg"); List.empty[MethodRule]
      case Error(msg, _)   => println(s"ERROR: $msg"); List.empty[MethodRule]
    }
  }
}
