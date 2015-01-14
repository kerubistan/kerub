package com.github.K0zka.kerub.model.dynamic

import java.util.UUID
import com.github.K0zka.kerub.model.Entity

/**
 * Dynamic general information about the status of a host.
 */
public class HostDynamic(
		override val id: UUID,
		var status : HostStatus,
		var controlNode : UUID
                        ) : Entity<UUID> {
}