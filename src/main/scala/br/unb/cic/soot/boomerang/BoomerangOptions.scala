package br.unb.cic.soot.boomerang

import boomerang.DefaultBoomerangOptions


class BoomerangOptions extends DefaultBoomerangOptions {
  override def onTheFlyCallGraph() = false
}
