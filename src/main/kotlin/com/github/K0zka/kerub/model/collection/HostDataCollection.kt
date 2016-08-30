package com.github.K0zka.kerub.model.collection

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.config.HostConfiguration
import com.github.K0zka.kerub.model.dynamic.HostDynamic

data class HostDataCollection(
		override val stat: Host,
		override val dynamic: HostDynamic? = null,
		val config: HostConfiguration? = null
) : DataCollection<Host, HostDynamic>