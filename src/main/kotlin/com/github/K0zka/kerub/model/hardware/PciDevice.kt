package com.github.K0zka.kerub.model.hardware

import org.hibernate.search.annotations.Field
import java.io.Serializable

/**
 * Represents a PCI device of the host.
 */
class PciDevice(
		@Field
		val address: String,
		@Field
		val devClass: String,
		@Field
		val vendor: String,
		@Field
		val device: String
)
	: Serializable
