package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic

data class VirtualStorageDataCollection(
		override val stat: VirtualStorageDevice,
		override val dynamic: VirtualStorageDeviceDynamic?
) : DataCollection<VirtualStorageDevice, VirtualStorageDeviceDynamic>