[Scala REPLを拡張するには](http://taisukeoe.github.io/blog/2014/12/06/scala-repl-extension)のサンプルコード。

This is an example project to customize Scala default REPL.

Although this project itself is not meaningful so much, I believe it's good starting point to create your own REPL in your project! :)

#What is different from Scala default REPL?
It shows

 * Class names when custom ClassLoader loads any classes.
 * ":myCommand" command on top of default REPL commands.

```scala
scala> val hello = "hello"
MyClassLoader loads classOf <root>.$line3
<<中略>>
MyClassLoader loads classOf scala.collection.mutable.StringBuilder
MyClassLoader loads classOf scala.runtime.ScalaRunTime$
hello: String = hello

scala> :myCommand hello
This is a custom command example. You can do something from value:"hello" with custom Scala interpreter.
```

#How to use:
```scala
$ git clone git:github.com/taisukeoe/MyCLRepl
$ cd MyCLRepl
$ sbt assembly
$ java -jar target/scala-2.11/MyClassLoaderREPL-assembly-0.1-SNAPSHOT.jar
```
