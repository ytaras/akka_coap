package akka.coap.server

import java.net.InetSocketAddress

import akka.NotUsed
import akka.actor._
import akka.coap.model.{Response, PreBuildCaliforniumMarshallers, Request}
import akka.http.scaladsl.Http
import akka.io.{IO, Udp}
import akka.stream.scaladsl.{Keep, Flow, Sink, Source}
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

final case class IncomingConnection(
                                   localAddress: InetSocketAddress,
                                   remoteAddress: InetSocketAddress,
                                   flow: Flow[Response, Request, NotUsed]
                                   ) {
  def handleWith[Mat](handler: Flow[Request, Response, Mat])(implicit mat: Materializer): Mat =
    flow.joinMat(handler)(Keep.right).run()

}


object Main extends App {
  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()
  val ic: IncomingConnection = ???
  ic.handleWith(Flow[Request].map(_ => ???))
  Coap.get(as).bind(1234).to(Sink.foreach { inputConnection =>

  })

  new CoapClient("coap://localhost:1234/").post("abc", 1)
}

