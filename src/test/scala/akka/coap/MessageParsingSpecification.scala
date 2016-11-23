package akka.coap

import org.scalacheck.{Arbitrary, Gen}
import org.specs2.{ScalaCheck, Specification}
import org.eclipse.californium.core.coap.{Message => CMessage, CoAP, EmptyMessage}

/**
  * Created by ytaras on 11/23/16.
  */
class MessageParsingSpecification extends Specification with ScalaCheck {

  def is =
    s2"""
         We can:
          - Parse message serialized by californium $serDeser
          - Serialize message parsable by californium
      """
  def serDeser = prop { (message: Message) =>
    message == parseWithScodec(serializeWithCalifornium(message))
  }

  def parseWithScodec(data: Array[Byte]): Message = {
    Message.parse(data).require
  }
  def serializeWithCalifornium(message: Message): Array[Byte] = {
    messageToCaliforium(message).getBytes
  }

  def messageToCaliforium(m: Message): CMessage = {
    val messageType = m.messageType match {
      case CON => CoAP.Type.CON
      case NON => CoAP.Type.NON
    }
    new EmptyMessage(messageType)
  }

  implicit val messageTypeArb: Arbitrary[MessageType] = Arbitrary(Gen.oneOf(CON, NON))
  implicit val messageArb = Arbitrary(Gen.resultOf(Message.apply _))

}
