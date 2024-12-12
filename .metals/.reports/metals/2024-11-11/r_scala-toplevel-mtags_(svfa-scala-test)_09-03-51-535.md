error id: file://<WORKSPACE>/src/test/scala/br/unb/cic/android/FlowdroidSpec.scala:[213..218) in Input.VirtualFile("file://<WORKSPACE>/src/test/scala/br/unb/cic/android/FlowdroidSpec.scala", "package br.unb.cic.android

import br.unb.cic.soot.JSVFATest
import br.unb.cic.soot.graph.{SinkNode, SourceNode}
import soot.{G, Scene}
import soot.options.Options

import scala.collection.JavaConverters._

trait class FlowdroidSpec {
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
    "<javax.servlet.http.mock.HttpServletRequest: java.lang.String getParameter(java.lang.String)>",
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

  override def getIncludeList(): List[String] = List(
      "java.lang.*",
      "javax.servlet.*",
      "java.util.*",
      "java.io.*"
    )

}")
file://<WORKSPACE>/src/test/scala/br/unb/cic/android/FlowdroidSpec.scala
file://<WORKSPACE>/src/test/scala/br/unb/cic/android/FlowdroidSpec.scala:10: error: expected identifier; obtained class
trait class FlowdroidSpec {
      ^
#### Short summary: 

expected identifier; obtained class