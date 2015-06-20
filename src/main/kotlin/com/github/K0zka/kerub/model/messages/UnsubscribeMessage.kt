package com.github.K0zka.kerub.model.messages

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName

JsonTypeName("unsubscribe")
JsonCreator
public class UnsubscribeMessage (JsonProperty("channel") val channel: String) : Message
