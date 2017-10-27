package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import org.infinispan.Cache
import java.util.UUID

class VirtualStorageDeviceDaoImpl(cache: Cache<UUID, VirtualStorageDevice>, eventListener: EventListener, auditManager: AuditManager)
	: VirtualStorageDeviceDao, AbstractAssetDao<VirtualStorageDevice>(cache, eventListener, auditManager) {
	override fun getEntityClass(): Class<VirtualStorageDevice> =
			VirtualStorageDevice::class.java
}