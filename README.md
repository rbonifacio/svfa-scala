# SVFA (Sparse Value Flow Analysis) implementation based on Soot

This is a scala implementation of a framework that builds a sparse-value flow graph using Soot.

## Status

   * experimental

## Usage

   * clone this repository or download an stable release
   * you will need to add a github token to your **~/.gitconfig**.
     ```
     [github]
             token = TOKEN
     ```
   * build this project using sbt (`sbt compile test`)
   * publish the artifact as a JAR file in your m2 repository (`sbt publish`)
   * create a dependency to the svfa-scala artifact in your maven project. 

```{xml}
<dependency>	
  <groupId>br.unb.cic</groupId>
  <artifactId>svfa-scala_2.12</artifactId>
  <version>0.0.2-SNAPSHOT</version>
 </dependency>
```

   * implement a class that extends the `JSVFA class` (see some examples in the scala tests). you must provide implementations to the following methods
      * `getEntryPoints()` to set up the "main" methods. This implementation must return a list of Soot methods
      * `sootClassPath()` to set up the soot classpath. This implementation must return a string
      * `analyze(unit)` to identify the type of a node  (source, sink, simple node) in the graph; given a statement (soot unit)


## Dependencies

This project use some of the [FlowDroid](https://github.com/secure-software-engineering/FlowDroid) test cases. The FlowDroid test cases in `src/test/java/securibench` are under [LGPL-2.1](https://github.com/secure-software-engineering/FlowDroid/blob/develop/LICENSE) license.

## Benchmark

Tests failed: 68, passed: 9, ignored: 31 of 108 test

### DSL
Tests failed: 0, passed: 3, ignored: 0 of 3 test


### Flowdroid
Tests failed: 42, passed: 0, ignored: 9 of 51 test
#### AliasingTest
Tests failed: 5, passed: 0, ignored: 4 of 9 test
#### BasicTest
Tests failed: 37, passed: 0, ignored: 5 of 42 test


### Graph
Tests failed: 0, passed: 6, ignored: 1 of 51 test


### Soot
Tests failed: 26, passed: 0, ignored: 21 of 47 test
