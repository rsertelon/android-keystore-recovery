/**
 * Android Keystore Recovery
 * Copyright (C) 2013 Romain Sertelon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.bluepyth.scala.akr.actor

import java.io.FileInputStream

import akka.actor._
import akka.routing._
import akka.contrib.throttle._
import akka.contrib.throttle.Throttler._

import fr.bluepyth.scala.akr.actor._
import fr.bluepyth.scala.akr.AKRConfig
import fr.bluepyth.scala.akr.generator.SimplePasswordGenerator
import fr.bluepyth.scala.akr.jks.JKSUtils

import scala.concurrent.duration._

class AppActor(config: AKRConfig) extends Actor {

  val inJKSUtils = new FileInputStream(config.keystore.get)
  implicit val jksUtils = new JKSUtils(inJKSUtils, new Array[Char](1))
  inJKSUtils.close

  val passGen = new SimplePasswordGenerator(config)

  val logger = context.actorOf(Props(new LoggerActor(self)), "logger")
  val router = context.actorOf(Props(new TryPasswordActor(logger)).withRouter(SmallestMailboxRouter(Runtime.getRuntime.availableProcessors)), "router")

  val throttler = config.passwordsPerSecond.map { mps =>
    val t = context.actorOf(Props(classOf[TimerBasedThrottler], mps msgsPer 1.second))
    t ! SetTarget(Some(self))
    t
  }
  
  context.watch(router)
  
  var continue = true

  def receive = {
    case StartApp => {
      logger ! StartingBruteForce("Starting Brute force of keystore located at " + config.keystore.get)
      self ! TryPassword
    }
    case TryPassword =>
      if (passGen.hasNext) {
        router ! Password(passGen.next)
        self ! Next
      } else {
        self ! StopApp
      }
    case Next =>
      if(continue) {
        throttler.getOrElse(self) ! TryPassword
      }
    case StopApp => {
      continue = false
      router ! Broadcast(PoisonPill)
    }
    case Terminated(router) => {
      logger ! PoisonPill
    }
  }
}