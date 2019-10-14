package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonView
import com.github.kerubistan.kerub.model.expectations.VirtualStorageDeviceReference
import com.github.kerubistan.kerub.model.expectations.VirtualStorageExpectation
import com.github.kerubistan.kerub.model.index.Indexed
import com.github.kerubistan.kerub.model.views.Simple
import com.github.kerubistan.kerub.utils.validateSize
import io.github.kerubistan.kroki.collections.join
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
		override val owner: AssetOwner? = null,

		override val recycling: Boolean = false

) : Entity<UUID>, Constrained<VirtualStorageExpectation>, Named, Asset, Recyclable, Indexed<VirtualStorageDeviceIndex> {

	init {
		size.validateSize("size")
	}

	override fun references(): Map<KClass<out Asset>, List<UUID>> {
		val mapOf: Map<KClass<out Asset>, List<UUID>> = mapOf(
				VirtualStorageDevice::class to expectations
						.filter { it is VirtualStorageDeviceReference }
						.map { (it as VirtualStorageDeviceReference).virtualStorageDeviceReferences }
						.join()
		)
		return mapOf.filter { it.value.isNotEmpty() }
	}

	@get:JsonIgnore
	override val index by lazy { VirtualStorageDeviceIndex(this) }

}