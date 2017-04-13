package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Host
import java.util.UUID

interface HostDao : ListableCrudDao<Host, UUID>, DaoOperations.SimpleSearch<Host> {
	fun byAddress(address : String) : List<Host>
}