package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.CpuStat
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.virt.virsh.Virsh
import com.github.K0zka.kerub.utils.toUUID
import org.apache.sshd.ClientSession
import java.math.BigInteger

class KvmHypervisor(private val client: ClientSession,
					private val host: Host,
					private val vmDynDao: VirtualMachineDynamicDao,
					private val virtualStorageDao: VirtualStorageDeviceDao,
					private val virtualStorageDynDao: VirtualStorageDeviceDynamicDao) : Hypervisor {

	companion object {
		val logger = getLogger(KvmHypervisor::class)
	}

	override fun startMonitoringProcess() {
		Virsh.domStat(client, {
			stats ->
			val vmDyns = vmDynDao.findByHostId(host.id)
			val runningVms = stats.map { it.name.toUUID() }

			//handle vms that no longer run on this host
			vmDyns.filterNot { it.id in runningVms }.forEach {
				vmDynDao.remove(it.id)
			}

			stats.forEach {
				stat ->
				val runningVmId = stat.name.toUUID()
				val dyn = vmDyns.firstOrNull { it.id == runningVmId } ?: VirtualMachineDynamic(
						id = runningVmId,
						status = VirtualMachineStatus.Up,
						memoryUsed = BigInteger.ZERO,
						hostId = host.id
				)
				val updated = dyn.copy(
						cpuUsage = stat.cpuStats.mapIndexed { i, vcpuStat -> CpuStat.zero.copy() },
						lastUpdated = System.currentTimeMillis(),
						hostId = host.id,
						memoryUsed = stat.balloonSize ?: BigInteger.ZERO
				)
				vmDynDao.update(updated)
			}

		})
	}

	override fun suspend(vm: VirtualMachine) {
		Virsh.suspend(client, vm.id)
	}

	override fun resume(vm: VirtualMachine) {
		Virsh.resume(client, vm.id)
	}

	override fun startVm(vm: VirtualMachine) {
		val storageMap = vm.virtualStorageLinks.map {
			storageLink ->
			storageLink to (
					requireNotNull(virtualStorageDao[storageLink.virtualStorageId])
							to
							requireNotNull(virtualStorageDynDao[storageLink.virtualStorageId])
					)
		}.toMap()
		Virsh.create(client, vm.id, vmDefinitiontoXml(vm, storageMap))
	}

	override fun stopVm(vm: VirtualMachine) {
		Virsh.destroy(client, vm.id)
	}

	override fun migrate(vm: VirtualMachine, source: Host, target: Host) {
		throw UnsupportedOperationException()
	}

}