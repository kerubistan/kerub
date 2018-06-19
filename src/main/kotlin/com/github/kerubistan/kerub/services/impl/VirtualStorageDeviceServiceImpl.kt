package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.VirtualStorageDeviceService
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg
import com.github.kerubistan.kerub.utils.now
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.scp.ScpTimestamp
import java.io.InputStream
import java.math.BigInteger
import java.nio.file.attribute.PosixFilePermission
import java.util.UUID
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.ok

class VirtualStorageDeviceServiceImpl(
		dao: VirtualStorageDeviceDao,
		accessController: AssetAccessController,
		private val dynDao: VirtualStorageDeviceDynamicDao,
		private val hostDao: HostDao,
		private val executor: HostCommandExecutor,
		private val vmDao: VirtualMachineDao
) : VirtualStorageDeviceService,
		AbstractAssetService<VirtualStorageDevice>(accessController, dao, "virtual disk") {

	override fun download(id: UUID, type: VirtualDiskFormat, async: AsyncResponse) {
		dao.update(id) { stat ->
			stat.copy(
					expectations =
					stat.expectations.filterNot { it is StorageAvailabilityExpectation }
							+ StorageAvailabilityExpectation(format = type)
			)
		}
		dynDao.waitFor(id) { dyn ->
			val host = requireNotNull(hostDao[dyn.allocation.hostId])
			async.resume(
					ok(executor.readRemoteFile(host, dyn.allocation.getPath(dyn.id)), MediaType.APPLICATION_OCTET_STREAM_TYPE).build()
			)
		}
	}

	override fun add(entity: VirtualStorageDevice): VirtualStorageDevice {
		return accessController.checkAndDo(asset = entity) {
			super.add(entity)
		} ?: entity
	}

	override fun load(id: UUID, type: VirtualDiskFormat, async: AsyncResponse, data: InputStream) {
		val device = getById(id)
		dynDao.waitFor(id) { dyn ->
			val host = requireNotNull(hostDao[dyn.allocation.hostId])
			executor.dataConnection(host) { session ->
				pump(data, device, dyn, session)

				val virtualSize = if (type != VirtualDiskFormat.raw) {
					val size: Long = QemuImg.info(
							session,
							"${(dyn.allocation as VirtualStorageFsAllocation).mountPoint}/${device.id}"
					).virtualSize
					BigInteger(
							"$size"
					)
				} else {
					device.size
				}

				dao.update(device.copy(
						expectations = device.expectations.filterNot { it is StorageAvailabilityExpectation },
						size = virtualSize
				))
				async.resume(null)
			}

		}

		dao.update(device.copy(
				expectations = device.expectations + StorageAvailabilityExpectation(format = type)
		))
	}

	override fun load(id: UUID, async: AsyncResponse, data: InputStream) {
		load(id, VirtualDiskFormat.raw, async, data)
	}

	private fun pump(data: InputStream, device: VirtualStorageDevice, dyn: VirtualStorageDeviceDynamic, session: ClientSession) {
		uploadRaw(data, device, dyn.allocation.getPath(dyn.id), session)
	}

	private fun uploadRaw(data: InputStream, device: VirtualStorageDevice, path: String, session: ClientSession) {
		session.createScpClient().upload(
				data,
				path,
				device.size.toLong(),
				listOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
				ScpTimestamp(now(), now())
		)
	}

	override fun beforeRemove(entity: VirtualStorageDevice) {
		vmDao.listByAttachedStorage(entity.id).let {
			check(it.isEmpty()) { "Disk attached to vms ${it.joinToString { it.id.toString() }}" }
		}
	}

	override fun doRemove(entity: VirtualStorageDevice) {
		// since the persistent data needs to be removed, we just mark this here for deletion
		dao.update(entity.copy(
				expectations = listOf(),
				recycling = true
		))
	}
}