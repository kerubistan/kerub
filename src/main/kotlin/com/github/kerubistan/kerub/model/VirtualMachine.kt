package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonView
import com.github.kerubistan.kerub.model.expectations.VirtualMachineExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineReference
import com.github.kerubistan.kerub.model.views.Detailed
import com.github.kerubistan.kerub.model.views.Simple
import com.github.kerubistan.kerub.utils.join
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass

/**
 * A virtual machine.
 */
@Indexed
@JsonTypeName("vm")
data class VirtualMachine constructor(
		@JsonView(Simple::class)
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		/**
		 * Name of the VM, which is not necessarily a unique identifier, but helps to find the VM.
		 */
		@Field
		@JsonView(Simple::class)
		@JsonProperty("name")
		override val name: String,

		@Field
		@JsonView(Simple::class)
		@JsonProperty("architecture")
		val architecture : String = "x86_64",

		/**
		 * The number of vCPUs of the VM.
		 */
		@Field
		@JsonView(Detailed::class)
		@JsonProperty("nrOfCpus")
		val nrOfCpus: Int = 1,
		/**
		 * Memory allocation, a range (minimum-maximum)
		 */
		@Field
		@JsonView(Simple::class)
		@JsonProperty("memory")
		val memory: Range<BigInteger> = Range(BigInteger("1024"), BigInteger("2048")),
		/**
		 * List of expectations against the VM.
		 */
		@Field
		@JsonView(Detailed::class)
		@JsonProperty("expectations")
		override
		val expectations: List<VirtualMachineExpectation> = listOf(),
		/**
		 * Storage devices of the VM
		 */
		@Field
		@JsonView(Detailed::class)
		@JsonProperty("virtualStorageLinks")
		val virtualStorageLinks: List<VirtualStorageLink> = listOf(),

		@Field
		@JsonView(Simple::class)
		@JsonProperty("owner")
		override val owner: AssetOwner? = null,

		@Field
		@JsonView(Detailed::class)
		@JsonProperty("devices")
		val devices: List<VirtualDevice> = listOf()

)
	: Entity<UUID>, Constrained<VirtualMachineExpectation>, Asset, Named {
	override fun references(): Map<KClass<out Asset>, List<UUID>> =
			mapOf<KClass<out Asset>, List<UUID>>(
					VirtualMachine::class to expectations
							.filter { it is VirtualMachineReference }
							.map { (it as VirtualMachineReference).referredVmIds }
							.join(),
					VirtualStorageDevice::class to virtualStorageLinks.map { it.virtualStorageId }
			).filter { it.value.isNotEmpty() }

	val virtualStorageIdStr: List<String>
		@Field
		get() = virtualStorageLinks.map { it.virtualStorageId.toString() }

	override fun toString(): String = "VM(id=$id,name=$name${if (expectations.isEmpty()) "" else "exp=" + expectations.size})"
}

