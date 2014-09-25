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
package fr.bluepyth.scala.akr.generator

import fr.bluepyth.scala.akr.{PasswordGenerator, AKRConfig}

import scala.io.Source

class WordlistPasswordGenerator(c: AKRConfig) extends PasswordGenerator {
  val worlistFile = Source.fromFile(c.wordlist.get)
  val wordlistFileSize = Source.fromFile(c.wordlist.get).getLines().size

  val fromLine = c.from.map(_.toInt - 1).getOrElse(0)
  val toLine = c.to.map(_.toInt).getOrElse(wordlistFileSize)

  val passwordsBlock = worlistFile.getLines().slice(fromLine, toLine)

  def hasNext = passwordsBlock.hasNext
  def next: Array[Char] = passwordsBlock.next().toCharArray


}
