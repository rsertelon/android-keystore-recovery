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
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import fr.bluepyth.scala.akr.jks.JKSUtils
import fr.bluepyth.scala.akr.message._
import java.io.File
import akka.actor.PoisonPill

class TryPasswordActor(logger: ActorRef)(implicit jksUtils: JKSUtils) extends Actor {
  def receive = {
    case Password(x) =>
      logger ! (if(jksUtils.keyIsRight(x)) PasswordFound(x.mkString) else TriedPassword(x))
  }
}