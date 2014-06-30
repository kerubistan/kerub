package com.github.K0zka.kerub.model.dynamic

import java.util.UUID
import com.github.K0zka.kerub.model.Entity

/**
 * Dynamic general information about the status of a host.
 */
public class HostDynamic : Entity<UUID> {
	override var id: UUID? = null
	var status : HostStatus? = null
	var controlNode : UUID? = null
}