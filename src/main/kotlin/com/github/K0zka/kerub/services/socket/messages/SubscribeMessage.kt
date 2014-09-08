package com.github.K0zka.kerub.services.socket.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

JsonTypeName("subscribe")
JsonCreator
public class SubscribeMessage(JsonProperty("channel") val channel: String) : Message {

}