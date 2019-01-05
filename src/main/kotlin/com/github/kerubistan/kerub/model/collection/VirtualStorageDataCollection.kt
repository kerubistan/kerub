package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import java.io.Serializable

data class VirtualStorageDataCollection(
		override val stat: VirtualStorageDevice,
		override val dynamic: VirtualStorageDeviceDynamic? = null
) : DataCollection<VirtualStorageDevice, VirtualStorageDeviceDynamic>, Serializable {
	init {
		this.validate()
		dynamic?.apply {
			check(stat.readOnly || allocations.size <= 1) {
				"only read only storage devices can have multiple allocations " +
						"- ${stat.id} has readOnly: ${stat.readOnly} and allocations $allocations"
			}
		}
	}
}