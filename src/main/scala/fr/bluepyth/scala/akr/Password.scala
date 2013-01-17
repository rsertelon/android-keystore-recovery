package fr.bluepyth.scala.akr

case class Password(p: Array[Char])

case class PasswordFound(p: String)

case class TriedPassword(p: Array[Char])