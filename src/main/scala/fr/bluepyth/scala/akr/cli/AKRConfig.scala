package fr.bluepyth.scala.akr.cli

case class AKRConfig(passwordLengthStart: Option[Int] = None, startAt: Option[String] = None, keystore: Option[String] = None)