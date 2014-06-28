package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement

/**
 *
 */
XmlRootElement(name = "host")
data class Host : Entity<UUID> {
	override var id : UUID? = null

	var address : String? = null

	var dedicated : Boolean = true

}