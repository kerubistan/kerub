package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.expectations.VirtualNetworkExpectation
import java.util.UUID

@JsonTypeName("vnet")
class VirtualNetwork(
		override val id: UUID,
		override val expectations: List<VirtualNetworkExpectation> = listOf(),
		val name : String
) : Entity<UUID>, Constrained<VirtualNetworkExpectation>