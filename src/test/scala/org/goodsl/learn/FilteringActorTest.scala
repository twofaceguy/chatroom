package org.goodsl.learn

/**
 * Created with IntelliJ IDEA.
 * User: Simon Xiao
 * Date: 13-7-26
 * Time: 下午3:52
 * Copyright goodsl.org 2012~2020
 */
import akka.testkit.TestKit
import akka.actor.{ Actor, Props, ActorRef, ActorSystem }
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.goodsl.StopSystemAfterAll

class FilteringActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpec
  with MustMatchers
  with StopSystemAfterAll {
  "A Filtering Actor" must {

    "filter out particular messages" in {
      import FilteringActorProtocol._
      val props = Props(new FilteringActor(testActor, 5))
      val filter = system.actorOf(props, "filter-1")
      filter ! Event(1)
      filter ! Event(2)
      filter ! Event(1)
      filter ! Event(3)
      filter ! Event(1)
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      filter ! Event(6)
      val eventIds = receiveWhile() {
        case Event(id) if id <= 5 => id
      }
      eventIds must be(List(1, 2, 3, 4, 5))
      expectMsg(Event(6))
    }

    "filter out particular messages using expectNoMsg" in {
      import FilteringActorProtocol._
      val props = Props(new FilteringActor(testActor, 5))
      val filter = system.actorOf(props, "filter-2")
      filter ! Event(1)
      filter ! Event(2)
      expectMsg(Event(1))
      expectMsg(Event(2))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(3)
      expectMsg(Event(3))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      expectMsg(Event(4))
      expectMsg(Event(5))
      expectNoMsg()
    }

  }
}

object FilteringActorProtocol {
  case class Event(id: Long)
}

class FilteringActor(nextActor: ActorRef,
                     bufferSize: Int) extends Actor {
  import FilteringActorProtocol._
  var lastMessages = Vector[Event]()
  def receive = {
    case msg: Event =>
      if (!lastMessages.contains(msg)) {
        lastMessages = lastMessages :+ msg
        nextActor ! msg
        if (lastMessages.size > bufferSize) {
          // discard the oldest
          lastMessages = lastMessages.tail
        }
      }
  }
}