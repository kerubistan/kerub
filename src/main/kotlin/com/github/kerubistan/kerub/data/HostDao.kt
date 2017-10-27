package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Host
import java.util.UUID

interface HostDao : ListableCrudDao<Host, UUID>, DaoOperations.SimpleSearch<Host> {
	fun byAddress(address: String): List<Host>
}