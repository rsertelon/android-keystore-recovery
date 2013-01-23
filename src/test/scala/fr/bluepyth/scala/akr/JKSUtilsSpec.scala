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

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.io.FileInputStream
import java.io.File
import java.security.MessageDigest
import java.io.DataInputStream
import java.security.DigestInputStream
import java.security.cert.Certificate

@RunWith(classOf[JUnitRunner])
class JKSUtilsSpec extends Specification {

  "isKeyRight" should {
    "return true when password is good" in {
      val jksUtils = new JKSUtils(getClass().getClassLoader().getResourceAsStream("keystore.jks"), new Array[Char](1))
      
      jksUtils.keyIsRight("AAAAAA".toCharArray) must beTrue
    }
    
    "return false when password is not good" in {
      val jksUtils = new JKSUtils(getClass().getClassLoader().getResourceAsStream("keystore.jks"), new Array[Char](1))
      
      jksUtils.keyIsRight("AAAAAB".toCharArray) must beFalse
    }
  }
  

}