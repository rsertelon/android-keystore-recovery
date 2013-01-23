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
package fr.bluepyth.scala.akr

import akka.actor.Actor
import java.io.FileInputStream
import akka.actor.ActorRef
import akka.actor.ActorLogging

class TryPasswordActor(keystore: String, loggerActor: ActorRef)(implicit jksUtils: JKSUtils) extends Actor with ActorLogging {
  def receive = {
    case Password(x) =>
      loggerActor ! (if(jksUtils.keyIsRight(x)) PasswordFound(x.mkString) else TriedPassword(x))
  }
}