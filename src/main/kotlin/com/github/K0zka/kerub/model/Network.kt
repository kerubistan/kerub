package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement

XmlRootElement(name = "network")
data class Network(
		override val id: UUID
                  )
: Entity<UUID>
