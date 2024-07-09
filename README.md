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


### Flowdroid Benchmark

###### failed: 33, passed: 71, ignored: 0 of 104 test (Original Benchmark)

> failed: 0, passed: 71, ignored: 33 of 104 test (68.27%)

- **AliasingTest** - failed: 0, passed: 5, ignored: 1 of 6 test `(83.33%)`
  - [5]

- **ArraysTest** - failed: 0, passed: 5, ignored: 5 of 10 test `(50%)`
  - [2]
  - [5]
  - [8]
  - [9]
  - [10]

- **BasicTest** - failed: 0, passed: 39, ignored: 3 of 42 test `(92.85%)`
  - [36]
  - [38]
  - [42]

- **CollectionTest** - failed: 0, passed: 2, ignored: 13 of 15 test `(13.33%)`
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

- **DataStructureTest** - failed: 0, passed: 5, ignored: 1 of 6 test `(83.33%)`
  - [5]

- **FactoryTest** - failed: 0, passed: 2, ignored: 1 of 3 test `(66.67%)`
  - [3]

- **InterTest** - failed: 0, passed:11, ignored: 4 of 14 test `(78.57%)`
  - [6]
  - [11] - flaky
  - [12]

- **SessionTest** - failed: 0, passed: 0, ignored: 3 of 3 test `(0%)`
  - [1]
  - [2]
  - [3]

- **StrongUpdateTest** - failed: 0, passed: 2, ignored: 3 of 5 test `(40%)`
  - [3]
  - [4]
  - [5]


