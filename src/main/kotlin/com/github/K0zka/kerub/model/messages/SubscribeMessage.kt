package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

JsonTypeName("subscribe")
JsonCreator
public class SubscribeMessage(JsonProperty("channel") val channel: String) : Message
