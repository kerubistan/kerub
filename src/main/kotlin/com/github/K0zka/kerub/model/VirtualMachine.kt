package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field

XmlRootElement(name = "vm")
data class VirtualMachine(
		DocumentId
		override val id: UUID,
		/**
		 * Name of the VM, which is not necesarily a unique identifier, but helps to find the VM.
		 */
		Field
		var name: String,
		/**
		 * The number of vCPUs of the VM.
		 */
		Field
		var nrOfCpus: Int,
		/**
		 * Memory allocation, a range (minimum-maximum)
		 */
		Field
		var memory: Range<Int> = Range(1024, 2048),
		/**
		 * List of expectations against the VM.
		 */
		var expectations: List<Expectation> = serializableListOf(),
		/**
		 * Disks of the VM
		 */
		var disks: List<Disk> = serializableListOf()
                         ) : Entity<UUID> {
}
