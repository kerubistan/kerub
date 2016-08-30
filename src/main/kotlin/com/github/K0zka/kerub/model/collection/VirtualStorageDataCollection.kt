package com.github.K0zka.kerub.model.collection

import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic

data class VirtualStorageDataCollection(
		override val stat: VirtualStorageDevice,
		override val dynamic: VirtualStorageDeviceDynamic?
) : DataCollection<VirtualStorageDevice, VirtualStorageDeviceDynamic>