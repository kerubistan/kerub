package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.data.ControllerConfigDao
import com.github.K0zka.kerub.data.DaoOperations
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.model.Entity
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import java.util.UUID

class GlobalOperationalStateBuilderImpl(
		private val hostDyn: HostDynamicDao,
		private val hostDao: HostDao,
		private val hostCfg: HostConfigurationDao,
		private val virtualStorageDao: VirtualStorageDeviceDao,
		private val virtualStorageDynDao: VirtualStorageDeviceDynamicDao,
		private val vmDao: VirtualMachineDao,
		private val vmDynDao: VirtualMachineDynamicDao,
		private val configDao: ControllerConfigDao
) : OperationalStateBuilder {

	internal fun <T : Entity<UUID>> retrieveAllRelated(
			dao: DaoOperations.Read<T, UUID>,
			ids: Collection<Entity<UUID>>): Promise<List<T>, Exception> {
		return task {
			dao.get(ids.map { it.id })
		}
	}

	override fun buildState(): OperationalState {
		val hosts = task { hostDao.list(start = 0, limit = Int.MAX_VALUE) }
		val vms = task { vmDao.list(start = 0, limit = Int.MAX_VALUE) }
		val vDisks = task { virtualStorageDao.list(start = 0, limit = Int.MAX_VALUE) }

		val hostDyns = retrieveAllRelated(hostDyn, hosts.get())
		val hostCfgs = retrieveAllRelated(hostCfg, hosts.get())
		val vmDys = retrieveAllRelated(vmDynDao, vms.get())
		val vStorageDyns = retrieveAllRelated(virtualStorageDynDao, vDisks.get())

		return OperationalState.fromLists(
				hosts = hosts.get(),
				hostDyns = hostDyns.get(),
				hostCfgs = hostCfgs.get(),
				vms = vms.get(),
				vmDyns = vmDys.get(),
				vStorage = vDisks.get(),
				vStorageDyns = vStorageDyns.get(),
				//this last thing is distributed cache and therefore the data comes from memory
				config = configDao.get()
		)
	}
}