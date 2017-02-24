package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.model.ControllerConfig
import com.github.K0zka.kerub.utils.emptyString
import org.infinispan.Cache

class ControllerConfigDaoImpl(private val cache: Cache<String, ControllerConfig>,
							  private val auditManager: AuditManager)
: ControllerConfigDao {
	override fun get(): ControllerConfig
			= cache.get(emptyString) ?: ControllerConfig()

	override fun set(config: ControllerConfig) {
		auditManager.auditUpdate(old = get(), new = config)
		cache.set(emptyString, config)
	}

}