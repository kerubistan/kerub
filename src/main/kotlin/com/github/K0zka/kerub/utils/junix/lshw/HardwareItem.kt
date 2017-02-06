package com.github.K0zka.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes(
		JsonSubTypes.Type(Bus::class),
		JsonSubTypes.Type(System::class)
)
interface HardwareItem {
	val description: String?
	val configuration: Map<String, String>?
	val children: List<HardwareItem>?
}