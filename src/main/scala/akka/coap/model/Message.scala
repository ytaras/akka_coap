package akka.coap.model

import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.{coap => californium}

/**
  * Created by ytaras on 11/25/16.
  */

sealed trait Message

case class Request(method: Method) extends Message

case class Response() extends Message

sealed abstract class Method(val rawCode: Int) {
}

object Method {
  def fromCode(code: Int) = code match {
    case 1 => GET
    case 2 => POST
    case 3 => PUT
    case 4 => DELETE
  }
}
case object POST extends Method(2)
case object GET extends Method(1)
case object PUT extends Method(3)
case object DELETE extends Method(4)
