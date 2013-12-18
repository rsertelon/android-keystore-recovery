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

import akka.actor.Actor
import akka.actor.ActorLogging
import fr.bluepyth.scala.akr.message._
import akka.actor.ActorRef
import akka.actor.PoisonPill

class LoggerActor(app: ActorRef) extends Actor with ActorLogging {

  var lastTried = System.nanoTime

  var numberOfTries = 0

  var passFound = false

  def receive = {
    case StartingBruteForce(message) =>
      log.info(message)
    case TriedPassword(p) =>
      if (System.nanoTime - lastTried < 30000000000L) {
        numberOfTries = numberOfTries + 1
      } else {
        log.info("Still searching at {} passwords/s... (last password: {})", numberOfTries / 30, p.mkString)
        lastTried = System.nanoTime
        numberOfTries = 0
      }
    case PasswordFound(p) =>
      log.info("Password was found! It is : {}", p)
      passFound = true
      app ! StopApp
  }

  override def postStop = {
    if (!passFound) {
      log.info("No password found, stopping application")
    }
    context.system.shutdown
  }
}