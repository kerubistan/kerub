package com.github.K0zka.kerub.model

import java.util.UUID

data class Network(
		override val id: UUID
                  )
: Entity<UUID>
