package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.VirtualStorageDevice
import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.Query
import java.util.UUID

public class VirtualStorageDeviceDaoImpl(cache: Cache<UUID, VirtualStorageDevice>, eventListener : EventListener)
: VirtualStorageDeviceDao, ListableIspnDaoBase<VirtualStorageDevice, UUID>(cache, eventListener) {
	override fun fieldSearch(
			field: String,
			value: String,
			start: Long,
			limit: Long): List<VirtualStorageDevice> {

		return Search.getQueryFactory(cache)
				.from(VirtualStorageDevice::class.java)
				.startOffset(start)
				.having(field).like("%${value}%").toBuilder<Query>()
				.maxResults(limit.toInt()).build()
				.list<VirtualStorageDevice>() as List<VirtualStorageDevice>

	}

	override fun getEntityClass(): Class<VirtualStorageDevice> =
		VirtualStorageDevice::class.java
}