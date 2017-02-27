package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.K0zka.kerub.security.AccessController
import com.github.K0zka.kerub.services.VirtualStorageDeviceService
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.scp.ScpTimestamp
import java.io.InputStream
import java.nio.file.attribute.PosixFilePermission
import java.util.UUID
import javax.ws.rs.container.AsyncResponse

class VirtualStorageDeviceServiceImpl(
		dao: VirtualStorageDeviceDao,
		accessController: AccessController,
		private val dynDao: VirtualStorageDeviceDynamicDao,
		private val hostDao: HostDao,
		private val executor: HostCommandExecutor
) : VirtualStorageDeviceService,
		AbstractAssetService<VirtualStorageDevice>(accessController, dao, "virtual disk") {

	override fun add(entity: VirtualStorageDevice): VirtualStorageDevice {
		return accessController.checkAndDo(asset = entity) {
			super.add(entity)
		} ?: entity
	}

	override fun load(id: UUID, async: AsyncResponse, data: InputStream) {
		val device = getById(id)
		dynDao.waitFor(id) {
			dyn ->
			val host = requireNotNull(hostDao[dyn.allocation.hostId])
			executor.dataConnection(host, {
				session ->
				pump(data, device, dyn, session)

				dao.update(device.copy(
						expectations = device.expectations.filterNot { it is StorageAvailabilityExpectation }
				))
				async.resume(null)
			})

		}

		dao.update(device.copy(
				expectations = device.expectations + StorageAvailabilityExpectation()
		))

	}

	private fun pump(data: InputStream, device: VirtualStorageDevice, dyn: VirtualStorageDeviceDynamic, session: ClientSession) {
		when (dyn.allocation) {
			is VirtualStorageLvmAllocation -> {
				uploadRaw(data, device, dyn.allocation.path, session)
			}
			is VirtualStorageGvinumAllocation -> {
				uploadRaw(data, device, "/dev/gvinum/${device.id}", session)
			}
			else -> {
				TODO()
			}
		}
	}

	private fun uploadRaw(data: InputStream, device: VirtualStorageDevice, path: String, session: ClientSession) {
		session.createScpClient().upload(
				data,
				path,
				device.size.toLong(),
				listOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
				ScpTimestamp(System.currentTimeMillis(), System.currentTimeMillis())
		)
	}
}