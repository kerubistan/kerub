package com.github.kerubistan.kerub.model.network

import com.fasterxml.jackson.annotation.JsonSubTypes
import java.io.Serializable

@JsonSubTypes(
		JsonSubTypes.Type(BondInterface::class),
		JsonSubTypes.Type(SimpleInterface::class)
)
interface NetworkInterface : Serializable {
	val name: String
	val portSpeedPerSec: Int
}
