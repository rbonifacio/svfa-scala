package br.unb.cic.flowdroid

import br.unb.cic.soot.JSVFATest
import br.unb.cic.soot.graph.{SinkNode, SourceNode}
import soot.{G, Scene}
import soot.options.Options

import scala.collection.JavaConverters._

abstract class FlowdroidSpec extends JSVFATest {
  val sinkList = List(
    "<java.io.PrintWriter: void println(java.lang.String)>",
    "<java.io.PrintWriter: void println(java.lang.Object)>",
    "<java.sql.Connection: java.sql.PreparedStatement prepareStatement(java.lang.String)>",
    "<java.sql.Statement: boolean execute(java.lang.String)>",
    "<java.sql.Statement: int executeUpdate(java.lang.String)>",
    "<java.sql.Statement: int executeUpdate(java.lang.String,int)>",
    "<java.sql.Statement: int executeUpdate(java.lang.String,java.lang.String[])>",
    "<java.sql.Statement: java.sql.ResultSet executeQuery(java.lang.String)>",
    "<javax.servlet.http.HttpServletResponse: void sendRedirect(java.lang.String)>",
    "<java.io.File: void <init>(java.lang.String)>",
    "<java.io.FileWriter: void <init>(java.lang.String)>",
    "<java.io.FileInputStream: void <init>(java.lang.String)>",
    "<securibench.micro.aliasing.Aliasing8: void sink(java.lang.String[])>"
  )

  val sourceList = List(
    "<javax.servlet.ServletRequest: java.lang.String getParameter(java.lang.String)>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getParameter(java.lang.String)>",
    "<javax.servlet.ServletRequest: java.lang.String[] getParameterValues(java.lang.String)>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String[] getParameterValues(java.lang.String)>",
    "<javax.servlet.ServletRequest: java.util.Map getParameterMap()>",
    "<javax.servlet.http.HttpServletRequest: java.util.Map getParameterMap()>",
    "<javax.servlet.ServletConfig: java.lang.String getInitParameter(java.lang.String)>",
    "<soot.jimple.infoflow.test.securibench.supportClasses.DummyServletConfig: java.lang.String getInitParameter(java.lang.String)>",
    "<javax.servlet.ServletConfig: java.util.Enumeration getInitParameterNames()>",
    "<javax.servlet.ServletContext: java.lang.String getInitParameter(java.lang.String)>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getParameter(java.lang.String)>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String[] getParameterValues(java.lang.String)>",
    "<javax.servlet.http.HttpServletRequest: java.util.Map getParameterMap()>",
    "<javax.servlet.http.HttpServletRequest: javax.servlet.http.Cookie[] getCookies()>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getHeader(java.lang.String)>",
    "<javax.servlet.http.HttpServletRequest: java.util.Enumeration getHeaders(java.lang.String)>",
    "<javax.servlet.http.HttpServletRequest: java.util.Enumeration getHeaderNames()>",
    "<javax.servlet.ServletRequest: java.lang.String getProtocol()>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getProtocol()>",
    "<javax.servlet.ServletRequest: java.lang.String getScheme()>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getScheme()>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getAuthType()>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getQueryString()>",
    "<javax.servlet.http.HttpServletRequest: java.lang.String getRemoteUser()>",
    "<javax.servlet.http.HttpServletRequest: java.lang.StringBuffer getRequestURL()>",
    "<javax.servlet.http.HttpServletRequest: javax.servlet.ServletInputStream getInputStream()>",
    "<javax.servlet.ServletRequest: javax.servlet.ServletInputStream getInputStream()>",
    "<com.oreilly.servlet.MultipartRequest: java.lang.String getParameter(java.lang.String)>"
  )

  override def configureSoot() {
    G.reset()
    Options.v().set_whole_program(true)
    Options.v().setPhaseOption("cg.spark", "on")
    Options.v().setPhaseOption("cg", "library:any-subtype")
    Options.v().set_output_format(Options.output_format_none)
    Options.v().set_no_bodies_for_excluded(true)
    Options.v().set_allow_phantom_refs(true)
    Options.v().set_keep_line_number(true)
    Options.v().set_prepend_classpath(true)
    Options.v().set_soot_classpath(sootClassPath())
    Options.v().set_process_dir(applicationClassPath().asJava)
    Options.v().set_include(getIncludeList().asJava);
    Options.v().set_full_resolver(true)
    Scene.v().loadNecessaryClasses()
    Scene.v().setEntryPoints(getEntryPoints().asJava)
  }

  def getIncludeList(): List[String] = {
    val includeList = List(
      "java.lang.AbstractStringBuilder",
      "java.lang.Boolean",
      "java.lang.Byte",
      "java.lang.Class",
      "java.lang.Integer",
      "java.lang.Long",
      "java.lang.Long",
      "java.lang.String",
      "java.lang.StringCoding",
      "java.lang.StringIndexOutOfBoundsException"
    )
    return includeList
  }

  override def svgToDotModel(): String = {
    val s = new StringBuilder
    var nodeColor = ""
    s ++= "digraph { \n"

    for(n <- svg.nodes()) {
      nodeColor = n.nodeType match  {
        case SourceNode => "[fillcolor=blue, style=filled]"
        case SinkNode   => "[fillcolor=red, style=filled]"
        case _          => ""
      }

      s ++= " " + "\"" + n.stmt + "\"" + nodeColor + "\n"

      s ++= "\t{\n"

      val adjacencyList = svg.map.get(n).get
      //      val edges = adjacencyList.map(next => "\"" + n.stmt + "\"" + " -> " + "\"" + next.stmt + "\"")
      val edges = adjacencyList.map(next => " -> " + "\"" + next.stmt + "\"")
      for(e <- edges) {
        s ++= "\t\t" + " " + e + "\n"
      }

      s ++= "\t}\n"
    }

    s ++= "}"

    return s.toString()
  }

  def jimpleOfMethod(): Unit = {
    println(this.getEntryPoints().head.retrieveActiveBody().toString())
  }
}