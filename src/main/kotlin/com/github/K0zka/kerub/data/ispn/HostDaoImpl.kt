package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.model.Host
import org.infinispan.Cache
import java.util.UUID

class HostDaoImpl(cache: Cache<UUID, Host>, eventListener: EventListener, auditManager: AuditManager)
	: ListableIspnDaoBase<Host, UUID>(cache, eventListener, auditManager), HostDao {
	override fun byAddress(address: String): List<Host> =
			cache.fieldEq(Host::address.name, address, 0, Int.MAX_VALUE)

	override fun fieldSearch(field: String, value: String, start: Long, limit: Int): List<Host> =
			cache.fieldSearch(field, value, start, limit)

	override fun getEntityClass(): Class<Host> = Host::class.java
}