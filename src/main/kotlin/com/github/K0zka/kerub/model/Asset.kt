package com.github.K0zka.kerub.model

import java.util.UUID
import kotlin.reflect.KClass

/**
 * An object that can be owned by an account or a project.
 */
interface Asset : Entity<UUID> {
	val owner: AssetOwner?
	fun references(): Map<KClass<*>, List<UUID>>
}