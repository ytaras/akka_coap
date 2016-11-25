package akka.coap.server

import java.net.InetSocketAddress

import akka.actor.{ActorLogging, Actor, ActorRef}
import akka.io.{Udp, IO}

/**
  * Created by ytaras on 11/25/16.
  */
class UdpCoapListener(nextActor: ActorRef, bindAddress: InetSocketAddress) extends Actor with ActorLogging {
  import context.system
  IO(Udp) ! Udp.Bind(self, bindAddress)
  def ready(socket: ActorRef): Receive = {
    case x: Udp.Received =>
      log.debug("Received: {}", x)
      nextActor forward x
  }

  override def receive: Actor.Receive = {
    case x: Udp.Bound =>
      log.debug("Bound: {}", x)
      context.become(ready(sender()))
  }
}
