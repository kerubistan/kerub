package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao

class GlobalOperationalStateBuilderImpl(
		private val hostDyn: HostDynamicDao,
		private val hostDao: HostDao,
		private val virtualStorageDao: VirtualStorageDeviceDao,
		private val virtualStorageDynDao: VirtualStorageDeviceDynamicDao,
		private val vmDao: VirtualMachineDao,
		private val vmDynDao: VirtualMachineDynamicDao

) : OperationalStateBuilder {
	override fun buildState(): OperationalState {
		val hosts = hostDao.list(start = 0, limit = Int.MAX_VALUE.toLong())
		val vms = vmDao.list(start = 0, limit = Int.MAX_VALUE.toLong())
		val vDisks = virtualStorageDao.list(start = 0, limit = Int.MAX_VALUE.toLong())
		return OperationalState.fromLists(
				hosts = hosts,
				hostDyns = hosts.map { hostDyn.get(it.id) }.filterNotNull(),
				vms = vms,
				vmDyns = vms.map { vmDynDao.get(it.id) }.filterNotNull(),
				vStorage = vDisks,
				vStorageDyns = vDisks.map { virtualStorageDynDao.get(it.id) }.filterNotNull()
		)
	}
}