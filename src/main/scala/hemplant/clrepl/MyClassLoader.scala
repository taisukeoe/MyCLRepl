package hemplant.clrepl

import scala.reflect.internal.util.ScalaClassLoader
import scala.tools.nsc.interpreter.JPrintWriter
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util

class MyClassLoader(root:AbstractFile,parent:ClassLoader,out:JPrintWriter) extends util.AbstractFileClassLoader(root,parent) with ScalaClassLoader{
  override def loadClass(name: String): Class[_] = {
    out.println(s"MyClassLoader loads classOf ${name}")
    super.loadClass(name)
  }
}
