package akka.coap.model

import akka.http.scaladsl.marshalling.{Marshaller, Marshalling}
import akka.http.scaladsl.util.FastFuture
import akka.io.Udp
import org.eclipse.californium.core.network.serialization.UdpDataParser
import org.eclipse.californium.core.{coap => californium}
import org.eclipse.californium.elements.RawData

import scala.util.Try

/**
  * Currently serialization/deserialization is implemented with Californium framework.
  * This is converter between Californium and akka-coap
  */
trait CaliforniumMarshallers {
  type FromUdpMarshaller[T] = Marshaller[Udp.Received, T]

  def akkaToCaliforniumRequest(x: Request) = {
    val method = x.method match {
      case POST => californium.CoAP.Code.POST
    }
    new californium.Request(method)
  }

  def californiumToAkkaRequest(x: californium.Request) = {
    Request(method = Method.fromCode(x.getRawCode))
  }

  private val updParser = new UdpDataParser()

  val udpToCalRequest: Marshaller[Udp.Received, californium.Request] = tryOpaque {
    case Udp.Received(data, sender) => Try {
      val message = updParser.parseMessage(new RawData(data.toArray, sender))
      message.asInstanceOf[californium.Request]
    }
  }

  implicit val udpRequestM: FromUdpMarshaller[Request] =
    udpToCalRequest.map(californiumToAkkaRequest)

  // Isn't it a part of akka http lib?
  private def tryOpaque[A, B](f: A => Try[B]): Marshaller[A, B] = Marshaller { implicit ec =>
    a => FastFuture(f(a))
      .map(value => List(Marshalling.Opaque(() => value)))
  }

}

object PreBuildCaliforniumMarshallers extends CaliforniumMarshallers