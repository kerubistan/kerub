package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.TemplateDao
import com.github.kerubistan.kerub.model.Template
import org.infinispan.Cache
import java.util.UUID

class TemplateDaoImpl(cache: Cache<UUID, Template>, eventListener: EventListener, auditManager: AuditManager) :
		TemplateDao, AbstractAssetDao<Template>(cache, eventListener, auditManager) {
	override fun getEntityClass(): Class<Template> = Template::class.java
}