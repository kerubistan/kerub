package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic

data class HostDataCollection(
		override val stat: Host,
		override val dynamic: HostDynamic? = null,
		val config: HostConfiguration? = null
) : DataCollection<Host, HostDynamic> {
	init {
		dynamic?.apply {
			check(id == stat.id) {"stat (${stat.id}) and dyn ($id) ids must match"}
		}
		config?.apply {
			check(id == stat.id) {"stat (${stat.id}) and config ($id) ids must match"}
		}
	}
}