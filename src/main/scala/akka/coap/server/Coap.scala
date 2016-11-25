package akka.coap.server

import java.net.InetSocketAddress

import akka.actor._
import akka.coap.model.{PreBuildCaliforniumMarshallers, Request}
import akka.io.{IO, Udp}
import akka.stream.scaladsl.Source
import akka.stream.{Materializer, ActorMaterializer, OverflowStrategies}
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.{coap => californiuum}
import org.eclipse.californium.core.network.serialization.UdpDataParser
import org.eclipse.californium.elements.RawData
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._

/**
  * Created by ytaras on 11/25/16.
  */
object Coap extends ExtensionId[CoapExt] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): CoapExt = new CoapExt

  override def lookup() = Coap
}

class CoapExt extends Extension {
  case class Stopper(ourListener: ActorRef)

  def bind(port: Int)(implicit as: ActorSystem, mat: Materializer): Source[Request, Stopper] = {
    import PreBuildCaliforniumMarshallers._
    import as.dispatcher
    val addr = new InetSocketAddress("localhost", port)
    val source = Source.actorRef[Udp.Event](10000, OverflowStrategies.DropTail)
    source.mapMaterializedValue { publisher =>
      val sendTo = as.actorOf(Props(new UdpCoapListener(publisher, addr)))
      Stopper(sendTo)
    }.mapAsync(4) {
      case x: Udp.Received => Marshal(x).to[Request]
    }
  }
}


object Main extends App {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  Coap.get(as).bind(1234).runForeach(println)

  new CoapClient("coap://localhost:1234/").post("abc", 1)
}

