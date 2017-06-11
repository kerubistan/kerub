package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.history.HistoryEntry
import com.github.K0zka.kerub.model.history.PropertyChange
import org.infinispan.Cache
import java.util.UUID

class VirtualMachineDynamicHistoryDaoImpl(cache: Cache<UUID, HistoryEntry>)
	: GenericHistoryDaoImpl<VirtualMachineDynamic>(cache){
	override fun changes(oldEntry: VirtualMachineDynamic, newEntry: VirtualMachineDynamic): List<PropertyChange>
			= diff(oldEntry, newEntry)
}