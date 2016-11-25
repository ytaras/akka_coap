package akka.coap.model

/**
  * Created by ytaras on 11/25/16.
  */

sealed trait Message

case class Request(method: Method) extends Message
case class Response() extends Message

sealed trait Method
case object POST extends Method