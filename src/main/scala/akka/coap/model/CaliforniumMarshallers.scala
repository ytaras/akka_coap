package akka.coap.model

import akka.http.scaladsl.marshalling.Marshaller

import org.eclipse.californium.core.{coap => californium}

/**
  * Currently serialization/deserialization is implemented with Californium framework.
  * This is converter between Californium and akka-coap
  */
trait CaliforniumMarshallers {
  type ToCaliforniumMarshaller[T] = Marshaller[T, californium.Message]

  val requestMarshaller: ToCaliforniumMarshaller[Request] = Marshaller.opaque { x =>
    val method = x.method match {
      case POST => californium.CoAP.Code.POST
    }
    new californium.Request(method)
  }
}
