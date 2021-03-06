package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import java.math.BigInteger.ZERO
import java.util.UUID

data class HostDataCollection(
		override val stat: Host,
		override val dynamic: HostDynamic? = null,
		val config: HostConfiguration? = null
) : DataCollection<UUID, Host, HostDynamic> {

	fun updateDynamic(update: (HostDynamic) -> HostDynamic): HostDynamic =
			update(this.dynamic ?: HostDynamic(id = this.stat.id))

	fun updateWithDynamic(update: (HostDynamic) -> HostDynamic): HostDataCollection = this.copy(
			dynamic = update(this.dynamic ?: HostDynamic(id = this.stat.id))
	)


	init {
		this.validate()
		config?.apply {
			check(id == stat.id) { "stat (${stat.id}) and config ($id) ids must match" }
		}
		stat.capabilities?.storageCapabilities?.forEach { capability ->
			val freeCapacity = dynamic?.storageStatus
					?.firstOrNull { it.id == capability.id }
					?.freeCapacity
			check(freeCapacity ?: ZERO <= capability.size) {
				"free capacity ($freeCapacity) of ${stat.address}/${capability.id} " +
						"is more than total capability ${capability.size}"
			}
		}
	}
}