package fr.bluepyth.scala.akr

import akka.actor.Actor
import java.io.FileInputStream
import akka.actor.ActorRef
import akka.actor.ActorLogging

class TryPasswordActor(keystore: String, loggerActor: ActorRef) extends Actor with ActorLogging {

//  val inJKSUtils = new FileInputStream(keystore)
  //val jksUtils = JKSUtils(inJKSUtils, new Array[Char](1))
//  inJKSUtils.close

  def receive = {
    case Password(x) =>
      //loggerActor ! (if(jksUtils.keyIsRight(x)) PasswordFound(x.mkString) else TriedPassword(x))
      loggerActor ! (if(JKSJavaUtils.keyIsRight(x)) PasswordFound(x.mkString) else TriedPassword(x))
  }
}