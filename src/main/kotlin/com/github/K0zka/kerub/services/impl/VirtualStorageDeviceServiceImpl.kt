package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.use
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.K0zka.kerub.model.paging.SearchResultPage
import com.github.K0zka.kerub.services.VirtualStorageDeviceService
import com.github.K0zka.kerub.utils.copyFrom
import org.apache.sshd.client.session.ClientSession
import java.io.InputStream
import java.util.UUID
import javax.ws.rs.container.AsyncResponse

class VirtualStorageDeviceServiceImpl(
		dao: VirtualStorageDeviceDao,
		private val dynDao : VirtualStorageDeviceDynamicDao,
		private val hostDao : HostDao,
		private val executor: HostCommandExecutor
) : VirtualStorageDeviceService,
		ListableBaseService<VirtualStorageDevice>(dao, "virtual disk") {

	override fun load(id: UUID, async : AsyncResponse, data: InputStream) {
		val device = getById(id)
		dynDao.waitFor(id) {
			dyn ->
			val host = requireNotNull(hostDao[dyn.allocation.hostId])
			executor.dataConnection(host, {
				session ->
				pump(data, dyn, session)

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

	private fun pump(data: InputStream, dyn: VirtualStorageDeviceDynamic, session: ClientSession) {
		session.createSftpClient().use {
			sftp ->
			when (dyn.allocation) {
				is VirtualStorageLvmAllocation -> {
					sftp.write(dyn.allocation.path).use {
						dev ->
						dev.copyFrom(data)
					}
				}
				is VirtualStorageFsAllocation -> {

				}
			}
		}
	}

	override fun search(field: String, value: String, start: Long, limit: Long): SearchResultPage<VirtualStorageDevice> {
		val list = (dao as VirtualStorageDeviceDao).fieldSearch(
				field = field,
				value = value,
				start = start,
				limit = limit
		)
		return SearchResultPage(
				start = start,
				count = list.size.toLong(),
				result = list,
				total = dao.count().toLong(),
				searchby = field
		)
	}
}