/* NSC -- new Scala compiler
 * Copyright 2006-2013 LAMP/EPFL
 * @author  Lex Spoon
 */

package hemplant.clrepl

import scala.reflect.io.File
import scala.tools.nsc.GenericRunnerCommand.{AsJar, AsScript, AsObject,Error}
import scala.tools.nsc._

object MyMainGenericRunner extends MyMainGenericRunner {
  def main(args: Array[String]): Unit = {
    if (!process(args)) sys.exit(1)
  }
}

class MyMainGenericRunner {
  def errorFn(str: String, e: Option[Throwable] = None, isFailure: Boolean = true): Boolean = {
    if (str.nonEmpty) Console.err println str
    e foreach (_.printStackTrace())
    !isFailure
  }

  def process(args: Array[String]): Boolean = {
    val command = new GenericRunnerCommand(args.toList, (x: String) => errorFn(x))
    import command.{settings, howToRun, thingToRun, shortUsageMsg, shouldStopWithInfo}
    settings.usejavacp.value = true
    def sampleCompiler = new Global(settings) // def so it's not created unless needed

    def run(): Boolean = {
      def isE = !settings.execute.isDefault
      def dashe = settings.execute.value

      def isI = !settings.loadfiles.isDefault
      def dashi = settings.loadfiles.value

      // Deadlocks on startup under -i unless we disable async.
      if (isI)
        settings.Yreplsync.value = true

      def combinedCode = {
        val files = if (isI) dashi map (file => File(file).slurp()) else Nil
        val str = if (isE) List(dashe) else Nil

        files ++ str mkString "\n\n"
      }

      def runTarget(): Either[Throwable, Boolean] = howToRun match {
        case AsObject =>
          ObjectRunner.runAndCatch(settings.classpathURLs, thingToRun, command.arguments)
        case AsScript =>
          ScriptRunner.runScriptAndCatch(settings, thingToRun, command.arguments)
        case AsJar =>
          JarRunner.runJar(settings, thingToRun, command.arguments)
        case Error =>
          Right(false)
        case _ =>
          // We start the repl when no arguments are given.
          // taisukeoe(oeuia.t@gmail.com modifies below line) on 2014/12/7
          Right(new CLLooper process settings)
      }

      /** If -e and -i were both given, we want to execute the -e code after the
        * -i files have been included, so they are read into strings and prepended to
        * the code given in -e.  The -i option is documented to only make sense
        * interactively so this is a pretty reasonable assumption.
        *
        * This all needs a rewrite though.
        */
      if (isE) {
        ScriptRunner.runCommand(settings, combinedCode, thingToRun +: command.arguments)
      }
      else runTarget() match {
        case Left(ex) => errorFn("", Some(ex)) // there must be a useful message of hope to offer here
        case Right(b) => b
      }
    }

    if (!command.ok)
      errorFn(f"%n$shortUsageMsg")
    else if (shouldStopWithInfo)
      errorFn(command getInfoMessage sampleCompiler, isFailure = false)
    else
      run()
  }
}
