package com.github.K0zka.kerub.model

import java.io.Serializable
import org.hibernate.search.annotations.Field

/**
 * Represents a PCI device of the host.
 */
public class PciDevice(
		Field
		val address: String,
		Field
		val devClass: String,
		Field
		val vendor: String,
        Field
		val device: String
                      )
: Serializable
