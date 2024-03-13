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


## Installation

- Install Scala Plugin in IntelliJ IDEA
- Install Java 8 (Java JDK Path `/usr/lib/jvm/java-8-openjdk-amd64`)
```{bash}
  sudo apt install openjdk-8-jre-headless
  sudo apt install openjdk-8-jdk
```
- Clone the project:
```{bash}
    git clone https://github.com/rbonifacio/svfa-scala
```
- Add dependency: 
     - Download [servlet-api-2.5.jar](https://repo1.maven.org/maven2/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar) and move to `.m2/repository/javax/servlet/servlet-api/2.5/`
- Add GitHub token in `~/.gitconfig`
- IDE
  - Reload `sbt` 
  - Set Project's settings to work with Java 8
  - Build Project
  - Run test


### Flowdroid
TTests failed: 34, passed: 64, ignored: 6 of 104 test

<!-- ~~Tests failed: 40, passed: 64, ignored: 0 of 104 test~~

###### Tests failed: 40, passed: 64, ignored: 0 of 104 test
###### Tests failed: 33, passed: 71, ignored: 0 of 104 test (original)

###### Tests failed: +17.5%, passed: +9.86, ignored: 0 of 104 test (original)
Tests failed: 33, passed: 71, ignored: 0 of 104 test (original) -->

<!-- ##### [SUMMARY] Tests failed: +17.5%, passed: +9.86, ignored: 0 of 104 test (original) -->

#### AliasingTest
Tests failed: 0, passed: 5, ignored: 1 of 6 test
- [5]

#### ArraysTest
Tests failed: 9, passed: 1, ignored: 0 of 10 test
- [1]
- [2]
- [3]
- [4]
- [6]
- [7]
- [8]
- [9]
- [10]

#### BasicTest
Tests failed: 0, passed: 37, ignored: 5 of 42 test
- [6]
- [17]
- [36]
- [38]
- [42]

#### CollectionTest 
Tests failed: 14, passed: 1, ignored: 0 of 15 test
- [2]
- [3]
- [4]
- [5]
- [6]
- [7]
- [8]
- [9]
- [10]
- [11]
- [11b]
- [12]
- [13]
- [14]

#### DataStructureTest
Tests failed: 1, passed: 5, ignored: 0 of 6 test
- [2]

#### FactoryTest
Tests failed: 1, passed: 2, ignored: 0 of 3 test
- [3]

#### InterTest
Tests failed: 7, passed: 7, ignored: 0 of 14 test
- [4]
- [5]
- [6]
- [7]
- [11] unstable
- [12]

~~#### PredTest~~
~~Tests failed: 3, passed: 6, ignored: 0 of 9 test~~
~~#### ReflectionTest~~
~~Tests failed: 4, passed: 0, ignored: 0 of 4 test~~
~~#### SanitizerTest~~
~~Tests failed: 2, passed: 4, ignored: 0 of 6 test~~

#### SessionTest
Tests failed: 3, passed: 0, ignored: 0 of 3 test
- [1]
- [2]
- [3]

#### StrongUpdateTest
Tests failed: 1, passed: 4, ignored: 0 of 5 test
- [4]


