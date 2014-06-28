package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement

XmlRootElement(name = "network")
data class Network : Entity<UUID> {
	override var id: UUID? = null

}