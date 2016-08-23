package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.model.Host
import org.infinispan.Cache
import java.util.UUID

class HostDaoImpl(cache: Cache<UUID, Host>, eventListener: EventListener, auditManager: AuditManager)
: ListableIspnDaoBase<Host, UUID>(cache, eventListener, auditManager), HostDao {
	override fun getEntityClass(): Class<Host> = Host::class.java
}