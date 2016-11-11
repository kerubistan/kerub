package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.VirtualStorageDevice
import org.infinispan.Cache
import java.util.UUID

class VirtualStorageDeviceDaoImpl(cache: Cache<UUID, VirtualStorageDevice>, eventListener: EventListener, auditManager: AuditManager)
: VirtualStorageDeviceDao, AbstractAssetDao<VirtualStorageDevice>(cache, eventListener, auditManager) {

	override fun fieldSearch(
			field: String,
			value: String,
			start: Long,
			limit: Long): List<VirtualStorageDevice> =
			cache.fieldSearch(VirtualStorageDevice::class, field, value, start, limit)

	override fun getEntityClass(): Class<VirtualStorageDevice> =
			VirtualStorageDevice::class.java
}