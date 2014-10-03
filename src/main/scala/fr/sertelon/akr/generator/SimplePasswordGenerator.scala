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
package fr.sertelon.akr.generator

import fr.sertelon.akr.{PasswordGenerator, AKRConfig}

class SimplePasswordGenerator(c: AKRConfig) extends PasswordGenerator {

  val upperCaseLetters = Array[Char](
	'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
	'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
	'W', 'X', 'Y', 'Z')

  val lowerCaseLetters = Array[Char](
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
    'w', 'x', 'y', 'z')
    
  val numbers = Array[Char]('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
  
  val letters = (if(!c.lowerCase) upperCaseLetters else Array[Char]()) ++ 
  				(if (!c.upperCase) lowerCaseLetters else Array[Char]())
  
  val chars: Array[Char] = 
    (if(c.lettersOnly)
      letters
    else if (c.numbersOnly)
      numbers
    else
        letters ++ numbers) ++ c.extraCharacters.map { ec => ec.toCharArray }.getOrElse(Array())
  

  var firstPassGiven = false

  var currentPassword: Array[Int] =
    c.from.map {
      _.toCharArray.map { charToIndex }
    }.orElse {
      c.minLength.filter(_ > 0).map { size =>
        (for (i <- 0 until size) yield 0).toArray
      }
    }.getOrElse { Array(0) }

  def charToIndex(c: Char) = chars.indexOf(c)

  def indicesToPassword(indices: Array[Int]): Array[Char] = {
    for (i <- indices) yield chars(i)
  }

  def next: Array[Char] = {
    if (firstPassGiven) {
      var hasIncremented = false
      var incNextChar = false
      for (i <- (0 until currentPassword.size).reverse) {
        if (!hasIncremented || incNextChar) {
          val curValue = currentPassword(i)
          val newValue = (currentPassword(i) + 1) % (chars.size)
          incNextChar = curValue > newValue
          currentPassword(i) = newValue
          hasIncremented = true
        }
        if (incNextChar && i == 0) {
          currentPassword = 0 +: currentPassword
        }
      }
    } else {
      firstPassGiven = true
    }
    indicesToPassword(currentPassword)
  }

  def hasNext = c.to.map { _ != indicesToPassword(currentPassword).mkString }.getOrElse { true }

}
