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

# Installing steps

Install .....
```

```
Install IntelliJ IDEA Community Edition
```
sudo snap install intellij-idea-community --classic
```
Install Scala Plugin
```
https://www.jetbrains.com/help/idea/managing-plugins.html
```

## Ubuntu

Install Java (/usr/lib/jvm/*)
```
apt install openjdk-8-jdk-headless
```
Install SBT
```
https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html
```



## fedora
Install Java JRE
```
sudo dnf install java-1.8.0-openjdk.x86_64
```

Install Java JDK
```
sudo dnf install java-8-openjdk-devel
```

## IntelliJ set up

Set JDK (main)

```
file > Project Structure > Platform Settings > SDK > Add new SDK > Choose Java JDK path
```

Set JDK (For Projects)

```
file > Project Structure > Project Settings > Project > ........
```

Set JDK (For Modules)

```
file > Project Structure > Project Settings > Module > ........
```

## Build 

To start build the project
```
sbt compile test
```


## Dependencies

This project use some of the [FlowDroid](https://github.com/secure-software-engineering/FlowDroid) test cases. The FlowDroid test cases in `src/test/java/securibench` are under [LGPL-2.1](https://github.com/secure-software-engineering/FlowDroid/blob/develop/LICENSE) license.
