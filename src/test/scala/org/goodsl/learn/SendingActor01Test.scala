package org.goodsl.learn

/**
 * Created with IntelliJ IDEA.
 * User: Simon Xiao
 * Date: 13-7-26
 * Time: 下午3:08
 * Copyright goodsl.org 2012~2020
 */
import akka.testkit.TestKit
import akka.actor.{ Props, ActorRef, Actor, ActorSystem }
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
//import scala.concurrent.duration._
import com.goodsl.StopSystemAfterAll

class SendingActor01Test extends TestKit(ActorSystem("testsystem"))
  with WordSpec
  with MustMatchers
  with StopSystemAfterAll {


  "A Sending Actor" must {
    "send a message to an actor when it has finished" in {
      import Kiosk01Protocol._
      val props = Props(new Kiosk01(testActor))
      val sendingActor = system.actorOf(props, "kiosk1")
      val tickets = Vector(Ticket(1), Ticket(2), Ticket(3))
      val game = Game("Lakers vs Bulls", tickets)
      sendingActor ! game

      expectMsgPF() {
        case Game(_, tickets) =>
          tickets.size must be(game.tickets.size - 1)
      }
    }
  }

}

object Kiosk01Protocol {
  case class Ticket(seat: Int)
  case class Game(name: String, tickets: Seq[Ticket])
}

class Kiosk01(nextKiosk: ActorRef) extends Actor {
  import Kiosk01Protocol._
  def receive = {
    case game @ Game(_, tickets) =>
      nextKiosk ! game.copy(tickets = tickets.tail)
  }
}
