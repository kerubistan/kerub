package com.github.K0zka.kerub.model

import java.util.UUID

public class VirtualNetwork(
		override val id: UUID,
		override val expectations: List<Expectation>
) : Entity<UUID>, Constrained<Expectation>