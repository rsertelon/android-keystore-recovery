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

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import fr.bluepyth.scala.akr.AKRConfig

@RunWith(classOf[JUnitRunner])
class SimplePasswordGeneratorSpec extends Specification {
	"hasNext" should {
	  "be true if no 'to' is defined and 'from' is defined" in {
	    val config = AKRConfig(from = Some("A"))
	    
	    val gen = new SimplePasswordGenerator(config)
	    gen.hasNext must beTrue
	  }
	  "be false when 'to' is reached" in {
	    val config = AKRConfig(from = Some("A"), to = Some("B"))
	    
	    val gen = new SimplePasswordGenerator(config)
	    gen.next // Got A
	    gen.next // Got B
	    gen.hasNext must beFalse
	  }
	}
	
	"next" should {
	  "return Array('A') if no option is set" in {
	    val gen = new SimplePasswordGenerator(AKRConfig())
	    gen.next must beEqualTo(Array('A'))
	  }
	  
	  "return Array('A') if min length is zero" in {
	    val gen = new SimplePasswordGenerator(AKRConfig(minLength = Some(0)))
	    gen.next must beEqualTo(Array('A'))
	  }
	  
	  "return Array('A', ...) of length 'min length'" in {
	    val gen = new SimplePasswordGenerator(AKRConfig(minLength = Some(4)))
	    val p = gen.next
	    p.size must beEqualTo(4)
	    p must beEqualTo(Array('A', 'A', 'A', 'A'))
	  }
	  
	  "return Array('A', 'B', 'C') if from password is 'ABC'" in {
	    val gen = new SimplePasswordGenerator(AKRConfig(from = Some("ABC")))
	    gen.next must beEqualTo(Array('A', 'B', 'C'))
	  }
	  
	  "return Array('A', 'B', 'C') if from password is 'ABC' and min length is set" in {
	    val gen = new SimplePasswordGenerator(AKRConfig(from = Some("ABC"), minLength = Some(6)))
	    val p = gen.next
	    p.size must beEqualTo(3)
	    p must beEqualTo(Array('A', 'B', 'C'))
	  }
	}
}