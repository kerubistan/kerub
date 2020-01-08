package com.github.kerubistan.kerub.model.hardware

import org.hibernate.search.annotations.Field
import java.io.Serializable

/**
 * Represents a PCI device of the host.
 */
data class PciDevice(
		@Field
		val address: String,
		@Field
		val devClass: String,
		@Field
		val vendor: String,
		@Field
		val device: String
) : Serializable
