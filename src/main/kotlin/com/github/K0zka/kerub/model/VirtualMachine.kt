package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.model.views.Detailed
import com.github.K0zka.kerub.model.views.Simple
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import java.util.UUID

Indexed
JsonTypeName("vm")
data class VirtualMachine constructor(
		override
		JsonView(Simple::class)
		DocumentId
		JsonProperty("id")
		val id: UUID = UUID.randomUUID(),
		/**
		 * Name of the VM, which is not necessarily a unique identifier, but helps to find the VM.
		 */
		Field
		JsonView(Simple::class)
		JsonProperty("name")
		val name: String,
		/**
		 * The number of vCPUs of the VM.
		 */
		Field
		JsonView(Detailed::class)
		JsonProperty("nrOfCpus")
		val nrOfCpus: Int = 1,
		/**
		 * Memory allocation, a range (minimum-maximum)
		 */
		Field
		JsonView(Simple::class)
		JsonProperty("memoryMb")
		val memoryMb: Range<Int> = Range(1024, 2048),
		/**
		 * List of expectations against the VM.
		 */
		Field
		JsonView(Detailed::class)
		JsonProperty("expectations")
		val expectations: List<Expectation> = listOf(),
		/**
		 * Storage devices of the VM
		 */
		Field
		JsonView(Detailed::class)
		JsonProperty("storagedevices")
		val storageDevices: List<StorageDevice> = listOf()
                         )
: Entity<UUID>

