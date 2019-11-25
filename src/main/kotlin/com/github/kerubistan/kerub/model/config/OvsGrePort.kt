package com.github.kerubistan.kerub.model.config

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ovs-gre-port")
data class OvsGrePort(override val name: String, val remoteAddress: String) : OvsPort