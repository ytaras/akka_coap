package akka.coap.model

import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.{coap => californium}

/**
  * Created by ytaras on 11/25/16.
  */

sealed trait Message

case class Request(method: Method) extends Message

case class Response() extends Message

sealed trait Method

object Method {
  def fromCode(code: Int) = code match {
    case 1 => GET
    case 2 => POST
    case 3 => PUT
    case 4 => DELETE
  }
}
case object POST extends Method
case object GET extends Method
case object PUT extends Method
case object DELETE extends Method
