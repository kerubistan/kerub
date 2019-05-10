package com.github.kerubistan.kerub.utils.junix.lshw

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes(
		JsonSubTypes.Type(Bus::class),
		JsonSubTypes.Type(Bridge::class),
		JsonSubTypes.Type(Communication::class),
		JsonSubTypes.Type(Disk::class),
		JsonSubTypes.Type(Display::class),
		JsonSubTypes.Type(Generic::class),
		JsonSubTypes.Type(Input::class),
		JsonSubTypes.Type(System::class),
		JsonSubTypes.Type(Storage::class),
		JsonSubTypes.Type(Power::class),
		JsonSubTypes.Type(Memory::class),
		JsonSubTypes.Type(Multimedia::class),
		JsonSubTypes.Type(Processor::class),
		JsonSubTypes.Type(Volume::class),
		JsonSubTypes.Type(NetworkInterface::class)
)
interface HardwareItem {
	val id : String?
	val description: String?
	val configuration: Map<String, String>?
	val children: List<HardwareItem>?
}