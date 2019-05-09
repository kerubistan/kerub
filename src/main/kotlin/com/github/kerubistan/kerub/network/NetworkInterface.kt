package com.github.kerubistan.kerub.network

import org.codehaus.jackson.annotate.JsonSubTypes
import java.io.Serializable

@JsonSubTypes(
		JsonSubTypes.Type(BondInterface::class),
		JsonSubTypes.Type(SimpleInterface::class)
)
interface NetworkInterface : Serializable {
	val name: String
	val portSpeedPerSec: Int
}
