package br.unb.cic.soot.svfa

import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.android.{InfoflowAndroidConfiguration, SetupApplication}

trait AndroidSootConfiguration extends SootConfiguration {

  def apk: String

  def platform(): String
  override def configureSoot(): Unit = {
    val config = new InfoflowAndroidConfiguration
    config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA)
    config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination)
    config.getAnalysisFileConfig.setAndroidPlatformDir(platform)
    config.getAnalysisFileConfig.setTargetAPKFile(apk)
    config.setMergeDexFiles(true)
    config.setTaintAnalysisEnabled(true)
    config.setIgnoreFlowsInSystemPackages(true)
    config.setExcludeSootLibraryClasses(true)

    val flowDroid = new SetupApplication(config)

    flowDroid.constructCallgraph()
  }

}
