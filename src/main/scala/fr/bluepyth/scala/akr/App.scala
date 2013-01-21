package fr.bluepyth.scala.akr

import scopt.immutable.OptionParser
import fr.bluepyth.scala.akr.cli.AKRConfig
import akka.actor.ActorSystem
import akka.actor.Props
import akka.routing.SmallestMailboxRouter
import java.io.FileInputStream

/**
 * @author BluePyth
 */
object App {

  def main(args: Array[String]) {

    val parser = new OptionParser[AKRConfig]("akr", "1.x") {
      def options = Seq(
        opt("f", "from", "Starts brute force at given password") { (v: String, c: AKRConfig) => c.copy(from = Some(v)) },
        opt("t", "to", "brute force will stop at given password") { (v: String, c: AKRConfig) => c.copy(to = Some(v)) },
        intOpt("l", "min-length", "Starts brute force with a password of given length") { (v: Int, c: AKRConfig) => c.copy(passwordLengthStart = Some(v)) },
        arg("keystore", "The keystore that will be bruteforced") { (v: String, c: AKRConfig) => c.copy(keystore = Some(v)) })
    }

    parser.parse(args, AKRConfig()) map { config =>
      startBruteForce(config)
    } getOrElse {
      // arguments are bad, usage message will have been displayed
    }
  }

  def startBruteForce(c: AKRConfig) = {

    println("Starting Brute force of keystore located at " + c.keystore.get)

    val system = ActorSystem("bruteforce")

    val loggerActor =
      system.actorOf(Props[LoggerActor], "logger")

    val smallestMailboxRouter =
      system.actorOf(Props(new TryPasswordActor(c.keystore.get, loggerActor)).withRouter(SmallestMailboxRouter(Runtime.getRuntime.availableProcessors * 75)), "router")

    val passwordGenerator = new PasswordGenerator(c.from, c.to, c.passwordLengthStart)

    val inJKSUtils = new FileInputStream(c.keystore.get)
    JKSJavaUtils.engineLoad(inJKSUtils, new Array[Char](1))
    inJKSUtils.close

    while (passwordGenerator.hasNext && !system.isTerminated)
      smallestMailboxRouter ! Password(passwordGenerator.next)
  }

}
