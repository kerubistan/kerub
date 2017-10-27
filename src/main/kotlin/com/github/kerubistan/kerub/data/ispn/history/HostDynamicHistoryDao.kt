package com.github.kerubistan.kerub.data.ispn.history

import com.github.kerubistan.kerub.model.dynamic.DynamicEntity
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.history.HistoryEntry
import com.github.kerubistan.kerub.model.history.PropertyChange
import org.infinispan.Cache
import java.util.UUID
import kotlin.reflect.KClass

class HostDynamicHistoryDao(cache: Cache<UUID, HistoryEntry>)
	: GenericHistoryDaoImpl<HostDynamic>(cache) {

	override val dynClass: KClass<out DynamicEntity> = HostDynamic::class

	override fun changes(oldEntry: HostDynamic, newEntry: HostDynamic): List<PropertyChange> = diff(oldEntry, newEntry)
}