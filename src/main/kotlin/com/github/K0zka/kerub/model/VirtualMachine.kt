package com.github.K0zka.kerub.model

import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID

data class VirtualMachine(
		DocumentId
		override val id: UUID,
		/**
		 * Name of the VM, which is not necessarily a unique identifier, but helps to find the VM.
		 */
		Field
		override var name: String,
		/**
		 * The number of vCPUs of the VM.
		 */
		Field
		var nrOfCpus: Int,
		/**
		 * Memory allocation, a range (minimum-maximum)
		 */
		Field
		var memoryMb: Range<Int> = Range(1024, 2048),
		/**
		 * List of expectations against the VM.
		 */
		var expectations: List<Expectation> = listOf(),
		/**
		 * Storage devices of the VM
		 */
		var storageDevices: List<StorageDevice> = listOf()
                         )
: Entity<UUID>, Named

