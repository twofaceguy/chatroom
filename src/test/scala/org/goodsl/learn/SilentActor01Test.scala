package org.goodsl.learn

/**
 * Created with IntelliJ IDEA.
 * User: Simon Xiao
 * Date: 13-7-26
 * Time: 上午9:52
 * Copyright goodsl.org 2012~2020
 */

import org.scalatest.{WordSpec}
import org.scalatest.matchers.MustMatchers
import akka.testkit.{TestActorRef, TestKit}
import akka.actor._
import com.goodsl.StopSystemAfterAll

class SilentActor01Test extends TestKit(ActorSystem("testsystem"))
with WordSpec
with MustMatchers
with StopSystemAfterAll {

  "A Silent Actor" must {
    "change state when it receives a message, single threaded" in {

      import SilentActorProtocol._

      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state must (contain("whisper"))
    }
    "change state when it receives a message, multi-threaded" in {

      import SilentActorProtocol._

      val silentActor = system.actorOf(Props[SilentActor], "s3")
      silentActor ! SilentMessage("whisper1")
      silentActor ! SilentMessage("whisper2")
      silentActor ! GetState(testActor)
      expectMsg(Vector("whisper1", "whisper2"))
    }
  }

}

object SilentActorProtocol {

  case class SilentMessage(data: String)

  case class GetState(receiver: ActorRef)

}

class SilentActor extends Actor {

  import SilentActorProtocol._

  var internalState = Vector[String]()

  def receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data
    case GetState(receiver) => receiver ! internalState
  }

  def state = internalState
}

