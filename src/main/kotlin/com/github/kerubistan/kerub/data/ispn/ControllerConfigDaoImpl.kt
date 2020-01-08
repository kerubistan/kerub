package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.ControllerConfigDao
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.messages.EntityUpdateMessage
import com.github.kerubistan.kerub.utils.emptyString
import org.infinispan.Cache

class ControllerConfigDaoImpl(private val cache: Cache<String, ControllerConfig>,
							  private val auditManager: AuditManager,
							  private val eventListener: EventListener)
	: ControllerConfigDao {
	override fun get(): ControllerConfig = cache[emptyString] ?: ControllerConfig()

	override fun set(config: ControllerConfig) {
		auditManager.auditUpdate(old = get(), new = config)
		cache[emptyString] = config
		eventListener.send(EntityUpdateMessage(obj = config))
	}

}