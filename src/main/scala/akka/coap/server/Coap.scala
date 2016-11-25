package akka.coap.server

import java.net.InetSocketAddress

import akka.actor._
import akka.coap.model.Request
import akka.io.{IO, Udp}
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, OverflowStrategies}
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.{coap => californiuum}
import org.eclipse.californium.core.network.serialization.UdpDataParser
import org.eclipse.californium.elements.RawData

/**
  * Created by ytaras on 11/25/16.
  */
object Coap extends ExtensionId[CoapExt] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): CoapExt = new CoapExt

  override def lookup() = Coap
}

class CoapExt extends Extension {
  case class Stopper(ourListener: ActorRef)

  def bind(port: Int)(implicit as: ActorSystem): Source[Request, Stopper] = {
    val addr = new InetSocketAddress("localhost", port)
    val source = Source.actorRef[Udp.Event](10000, OverflowStrategies.DropTail)
    // TODO Marshall
    val dataParser = new UdpDataParser()
    source.mapMaterializedValue { publisher =>
      val sendTo = as.actorOf(Props(new UdpCoapListener(publisher, addr)))
      Stopper(sendTo)
    }.map {
      case Udp.Received(data, sender) =>
        Request.fromRaw(dataParser.parseMessage(
          new RawData(data.toArray, sender)
        ).asInstanceOf[californiuum.Request])
        // TODO Marshall
    }
  }
}

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
object Main extends App {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  Coap.get(as).bind(1234).runForeach(println)

  new CoapClient("coap://localhost:1234/").post("abc", 1)
}

