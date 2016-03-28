package com.github.K0zka.kerub.model

import com.github.K0zka.kerub.model.expectations.VirtualNetworkExpectation
import java.util.UUID

class VirtualNetwork(
		override val id: UUID,
		override val expectations: List<VirtualNetworkExpectation>
) : Entity<UUID>, Constrained<VirtualNetworkExpectation>