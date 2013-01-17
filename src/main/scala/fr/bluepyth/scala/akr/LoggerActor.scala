package fr.bluepyth.scala.akr

import akka.actor.Actor
import akka.actor.ActorLogging

class LoggerActor extends Actor with ActorLogging {
  
	var lastTried = System.nanoTime
	
	var numberOfTriesSinceLastTick = 0
  
	def receive = {
	  case TriedPassword(p) => 
	    if(System.nanoTime - lastTried < 30000000000L){
	      numberOfTriesSinceLastTick = numberOfTriesSinceLastTick + 1
	    } else {
	      log.info("Still searching at {} passwords/s... (last password: {})", numberOfTriesSinceLastTick / 30, p.mkString)
	      lastTried = System.nanoTime
	      numberOfTriesSinceLastTick = 0
	    }
	  case PasswordFound(p) =>
	    log.info("Password was found! It is : {}", p)
	    context.system.shutdown
	}
}