package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import org.infinispan.Cache
import java.util.*

public class VirtualStorageDeviceDaoImpl(cache: Cache<UUID, VirtualStorageDevice>, eventListener : EventListener)
: VirtualStorageDeviceDao, ListableIspnDaoBase<VirtualStorageDevice, UUID>(cache, eventListener) {
	override fun getEntityClass(): Class<VirtualStorageDevice> =
		VirtualStorageDevice::class
}