package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.model.expectations.VirtualStorageDeviceReference
import com.github.K0zka.kerub.model.expectations.VirtualStorageExpectation
import com.github.K0zka.kerub.model.views.Simple
import com.github.K0zka.kerub.utils.join
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass

@JsonTypeName("virtual-storage")
data class
VirtualStorageDevice(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@Field
		val size: BigInteger,
		@Field
		val readOnly: Boolean = false,
		@Field
		override
		val expectations: List<VirtualStorageExpectation> = listOf(),
		@Field
		override
		val name: String,

		@Field
		@JsonView(Simple::class)
		@JsonProperty("owner")
		override val owner: AssetOwner? = null

)
: Entity<UUID>, Constrained<VirtualStorageExpectation>, Named, Asset {
	override fun references(): Map<KClass<*>, List<UUID>> {
		val mapOf: Map<KClass<*>, List<UUID>> = mapOf(
				VirtualStorageDevice::class to expectations
						.filter { it is VirtualStorageDeviceReference }
						.map { (it as VirtualStorageDeviceReference).virtualStorageDeviceReferences }
						.join()
		)
		return mapOf.filter { it.value.isNotEmpty() }
	}
}