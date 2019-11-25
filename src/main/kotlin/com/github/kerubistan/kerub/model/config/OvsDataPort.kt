package com.github.kerubistan.kerub.model.config

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ovs-data-port")
data class OvsDataPort(override val name: String) : OvsPort {
}