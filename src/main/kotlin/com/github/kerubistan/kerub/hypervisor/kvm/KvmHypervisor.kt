package com.github.kerubistan.kerub.hypervisor.kvm

import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.CpuStat
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.services.PasswordProtected
import com.github.kerubistan.kerub.model.services.StorageService
import com.github.kerubistan.kerub.utils.KB
import com.github.kerubistan.kerub.utils.genPassword
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh
import com.github.kerubistan.kerub.utils.junix.virt.virsh.SecretType
import com.github.kerubistan.kerub.utils.junix.virt.virsh.Virsh
import com.github.kerubistan.kerub.utils.silent
import com.github.kerubistan.kerub.utils.toMap
import com.github.kerubistan.kerub.utils.toUUID
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

class KvmHypervisor(private val client: ClientSession,
					private val host: Host,
					private val hostDao: HostDao,
					private val hostCfgDao: HostConfigurationDao,
					private val hostDynamicDao: HostDynamicDao,
					private val vmDynDao: VirtualMachineDynamicDao,
					private val virtualStorageDao: VirtualStorageDeviceDao,
					private val virtualStorageDynDao: VirtualStorageDeviceDynamicDao) : Hypervisor {

	companion object {
		val logger = getLogger(KvmHypervisor::class)
		val kb = KB.toBigInteger()
	}

	override fun startMonitoringProcess() {
		Virsh.domStat(client, {
			stats ->
			val vmDyns = vmDynDao.findByHostId(host.id)
			val runningVms = stats.map { silent { it.name.toUUID() } }.filterNotNull()

			//handle vms that no longer run on this host
			vmDyns.filterNot { it.id in runningVms }.forEach {
				vmDynDao.remove(it.id)
			}

			stats.forEach {
				stat ->
				silent {
					val runningVmId = stat.name.toUUID()
					vmDynDao.update(runningVmId, retrieve = {
						vmDyns.firstOrNull { it.id == runningVmId } ?: VirtualMachineDynamic(
								id = runningVmId,
								status = VirtualMachineStatus.Up,
								memoryUsed = BigInteger.ZERO,
								hostId = host.id
						)
					}, change = {
						it.copy(
								cpuUsage = stat.cpuStats.map { vCpuStat ->
									CpuStat.zero.copy(
											user = vCpuStat.time?.toFloat() ?: 0f
									)
								},
								lastUpdated = System.currentTimeMillis(),
								hostId = host.id,
								memoryUsed = stat.balloonSize?.times(kb) ?: BigInteger.ZERO
						)
					})
				}
			}

		})
	}

	override fun suspend(vm: VirtualMachine) {
		Virsh.suspend(client, vm.id)
	}

	override fun resume(vm: VirtualMachine) {
		Virsh.resume(client, vm.id)
	}

	override fun startVm(vm: VirtualMachine, consolePwd: String) {
		val storageDeviceIds = vm.virtualStorageLinks.map { it.virtualStorageId }
		val storageDevices = virtualStorageDao[storageDeviceIds]
		val storageDevicesMap = storageDevices.toMap()
		val storageDeviceDyns = virtualStorageDynDao[storageDeviceIds]
		val storageDeviceDynMap = storageDeviceDyns.toMap()
		val hostIds = storageDeviceDyns.map { it.allocation.hostId }
		val storageHosts = hostDao[hostIds]
		val storageHostMap = storageHosts.toMap()
		val hostDyns = hostDynamicDao[hostIds]
		val hostDynMap = hostDyns.toMap()

		val storageMap: List<VirtualStorageLinkInfo> = vm.virtualStorageLinks.map {
			link ->
			val deviceDyn = requireNotNull(storageDeviceDynMap[link.virtualStorageId])
			VirtualStorageLinkInfo(
					link = link,
					device = VirtualStorageDataCollection(
							stat = requireNotNull(storageDevicesMap[link.virtualStorageId]),
							dynamic = deviceDyn
					),
					storageHost = HostDataCollection(
							stat = requireNotNull(storageHostMap[deviceDyn.allocation.hostId]),
							dynamic = requireNotNull(hostDynMap[deviceDyn.allocation.hostId]),
							config = hostCfgDao[deviceDyn.allocation.hostId]
					)
			)
		}

		storageMap.filter { it.storageHost.stat.id != host.id }.forEach {
			remoteDevice ->
			val cfg = remoteDevice.storageHost.config
			val service = cfg?.services?.firstOrNull { it is StorageService && it.vstorageId == remoteDevice.device.stat.id }

			if (service is PasswordProtected) {
				Virsh.setSecret(
						session = client,
						id = remoteDevice.device.stat.id,
						type = SecretType.iscsi,
						value = requireNotNull(service.password)
				)
			}
		}

		Virsh.create(client, vm.id, vmDefinitiontoXml(vm, storageMap, consolePwd, host))
	}

	override fun stopVm(vm: VirtualMachine) {
		Virsh.destroy(client, vm.id)
	}

	override fun migrate(vm: VirtualMachine, source: Host, target: Host) {
		try {
			OpenSsh.keyGen(session = client, password = genPassword())
			//TOdo: copy generated key to target server
			Virsh.migrate(session = client, id = vm.id, targetAddress = target.address)

		} finally {
			//TODO: remove openssh key from host

		}
	}

	override fun getDisplay(vm: VirtualMachine) =
			Virsh.getDisplay(session = client, vmId = vm.id)

}