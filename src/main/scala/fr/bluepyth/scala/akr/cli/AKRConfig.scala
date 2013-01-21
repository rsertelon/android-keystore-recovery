package fr.bluepyth.scala.akr.cli

case class AKRConfig(passwordLengthStart: Option[Int] = None, from: Option[String] = None, to: Option[String] = None, keystore: Option[String] = None)