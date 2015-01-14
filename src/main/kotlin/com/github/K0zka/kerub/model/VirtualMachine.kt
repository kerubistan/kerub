package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field

XmlRootElement(name = "vm")
data class VirtualMachine(
		DocumentId
		override val id: UUID,
		Field
		var name: String,
		Field
		var nrOfCpus: Int,
		Field
		var memory: Range<Int> = Range(1024, 2048),
		var expectations: List<Expectation> = listOf(),
		var disks: List<Disk> = listOf()
                         ) : Entity<UUID> {
}