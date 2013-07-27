package org.goodsl.chatroom

import akka.actor.{Actor, Props, ActorSystem}

/**
 * Created with IntelliJ IDEA.
 * User: Simon Xiao
 * Date: 13-7-27
 * Time: 下午1:29
 * Copyright goodsl.org 2012~2020
 */
object ChatApp extends App{

  val system = ActorSystem("SimonSystem")
  val lobbyActor = system.actorOf(Props[LobbyActor], name = "lobbyActor")

  system.stop(lobbyActor)

  //system.

  Thread.sleep(1000)

  system.shutdown()

}

class LobbyActor extends Actor {

  def receive = {
    case _ => ()
  }

  override def preStart {
    println( " lobby started!")
  }


  override def postStop(){
    println( " lobby stopped!")
  }

  override def preRestart(reason : Throwable, message :Option[Any]) {
    println( " before lobby restart!")

    super.preRestart(reason,message)
  }


  override def postRestart(reason : Throwable){
    println( " after lobby restart!")
  }
}
