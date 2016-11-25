package akka.coap.model

import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.{coap => californium}

/**
  * Created by ytaras on 11/25/16.
  */

sealed trait Message

case class Request(method: Method) extends Message

object Request {
  def fromRaw(message: californium.Request): Request = Request(
    message.getCode match {
      case CoAP.Code.POST => POST
    }
  )
}

case class Response() extends Message

sealed trait Method
case object POST extends Method