package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.controller.config.ControllerConfig
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage
import com.github.K0zka.kerub.utils.emptyString
import org.infinispan.Cache

class ControllerConfigDaoImpl(private val cache: Cache<String, ControllerConfig>,
							  private val auditManager: AuditManager,
							  private val eventListener : EventListener)
: ControllerConfigDao {
	override fun get(): ControllerConfig
			= cache.get(emptyString) ?: ControllerConfig()

	override fun set(config: ControllerConfig) {
		auditManager.auditUpdate(old = get(), new = config)
		cache.set(emptyString, config)
		eventListener.send(EntityUpdateMessage(obj = config))
	}

}