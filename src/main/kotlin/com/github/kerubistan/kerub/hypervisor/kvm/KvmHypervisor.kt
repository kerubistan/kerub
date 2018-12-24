package com.github.kerubistan.kerub.hypervisor.kvm

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.CpuStat
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.utils.KB
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.genPassword
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh
import com.github.kerubistan.kerub.utils.junix.virt.virsh.Virsh
import com.github.kerubistan.kerub.utils.now
import com.github.kerubistan.kerub.utils.silent
import com.github.kerubistan.kerub.utils.toUUID
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

class KvmHypervisor(private val client: ClientSession,
					private val host: Host,
					private val vmDynDao: VirtualMachineDynamicDao) : Hypervisor {

	companion object {
		private val kb = KB.toBigInteger()
	}

	override fun startMonitoringProcess() {
		Virsh.domStat(client) { stats ->
			val vmDyns = vmDynDao.findByHostId(host.id)
			val runningVms = stats.mapNotNull {
				silent(level = LogLevel.Off, actionName = "parse domain name as uuid") { it.name.toUUID() }
			}

			//handle vms that no longer run on this host
			vmDyns.filterNot { it.id in runningVms }.forEach {
				vmDynDao.remove(it.id)
			}

			stats.forEach { stat ->
				silent(level = LogLevel.Warning, actionName = "update stat data") {
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
								lastUpdated = now(),
								hostId = host.id,
								memoryUsed = stat.balloonSize?.times(kb) ?: BigInteger.ZERO
						)
					})
				}
			}

		}
	}

	override fun suspend(vm: VirtualMachine) {
		Virsh.suspend(client, vm.id)
	}

	override fun resume(vm: VirtualMachine) {
		Virsh.resume(client, vm.id)
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
}