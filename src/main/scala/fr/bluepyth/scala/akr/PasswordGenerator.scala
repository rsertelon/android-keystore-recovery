package fr.bluepyth.scala.akr

class PasswordGenerator(val fromPassword: Option[String], val toPassword: Option[String], val minSize: Option[Int]) extends Iterator[Array[Char]] {

  val chars = Array[Char](
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
    'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
    'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
    's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
    '3', '4', '5', '6', '7', '8', '9')
  
  var firstPassGiven = false
    
  var currentPassword: Array[Int] =
    fromPassword.map {
      _.toCharArray.map { charToIndex(_) }
    }.orElse {
      minSize.map { size =>
        (for (i <- 0 until size) yield 0).toArray
      }
    }.getOrElse { Array(0) }

  def charToIndex(c: Char) = chars.indexOf(c)

  def indicesToPassword(indices: Array[Int]): Array[Char] = {
    for (i <- indices) yield chars(i)
  }

  def next: Array[Char] = {
    if(firstPassGiven) {
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
	      if(incNextChar && i == 0) {
	        currentPassword = 0 +: currentPassword
	      }
	    }
    } else {
      firstPassGiven = true
    }
    indicesToPassword(currentPassword)
  }

  def hasNext = toPassword.map { _ == currentPassword.toString }.getOrElse { true } 

}
