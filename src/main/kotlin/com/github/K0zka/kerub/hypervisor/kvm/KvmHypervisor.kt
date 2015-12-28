package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
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

class KvmHypervisor(private val client: ClientSession, private val host : Host, private val vmDynDao : VirtualMachineDynamicDao) : Hypervisor {

	companion object {
		val logger = getLogger(KvmHypervisor::class)
	}

	override fun startMonitoringProcess() {
Virsh.domStat(client, {
			stats ->
			stats.map {
				vmStat ->
				val id = vmStat.name.toUUID()
				val dyn = vmDynDao[id] ?: VirtualMachineDynamic(
						id = id,
						status = VirtualMachineStatus.Up,
						hostId = host.id,
						memoryUsed = BigInteger.ZERO
				)
				dyn.copy(
						status = VirtualMachineStatus.Up,
						memoryUsed = vmStat.balloonSize ?: BigInteger.ZERO,
						//TODO
						cpuUsage = vmStat.cpuStats.mapIndexed { i, vcpuStat -> CpuStat.zero.copy(
								cpuNr = i,
								user = vcpuStat.time?.toFloat() ?: 0f
						)
						},
						lastUpdated = System.currentTimeMillis(),
						hostId = host.id
				)
			}.forEach {
				vmDyn ->
				vmDynDao.update(vmDyn)
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
		Virsh.create(client, vm.id, vmDefinitiontoXml(vm))
	}

	override fun stopVm(vm: VirtualMachine) {
		Virsh.destroy(client, vm.id)
	}

	override fun migrate(vm: VirtualMachine, source: Host, target: Host) {
		throw UnsupportedOperationException()
	}

}