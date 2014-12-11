package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Host
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

JsonCreator
public data class HostAndPassword(JsonProperty("host") val host : Host, JsonProperty("password") val password : String) {
}