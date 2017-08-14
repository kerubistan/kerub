package com.github.K0zka.kerub.model

import java.util.UUID
import kotlin.reflect.KClass

/**
 * A template of virtual machines.
 */
data class Template(
		override val id: UUID,
		override val name: String,
		override val owner: AssetOwner?,
		val vmNamePrefix: String,
		val vm: VirtualMachine
) : Entity<UUID>, Named, Asset {
	override fun references(): Map<KClass<out Asset>, List<UUID>> = vm.references()
}