package com.github.K0zka.kerub.services

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.K0zka.kerub.model.Host

public data class HostAndPassword @JsonCreator constructor(
        @JsonProperty("host")
        val host: Host,
        @JsonProperty("password")
        val password: String
)