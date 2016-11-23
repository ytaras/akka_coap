package akka.coap

import scodec.{Attempt, Codec}
import scodec.bits.{BitVector, ByteVector}

/**
  * Created by ytaras on 11/23/16.
  */
case class Message(messageType: MessageType)

object Message {

  def parse(data: Array[Byte]): Attempt[Message] = {
    val bytes = BitVector(data)
    codec.decodeValue(bytes)
  }

  val codec: Codec[Message] = ???

}

sealed trait MessageType
case object CON extends MessageType
case object NON extends MessageType
