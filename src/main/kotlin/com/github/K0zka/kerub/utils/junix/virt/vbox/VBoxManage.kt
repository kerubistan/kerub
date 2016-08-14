package com.github.K0zka.kerub.utils.junix.virt.vbox

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.io.DeviceType
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.utils.MB
import com.github.K0zka.kerub.utils.storage.iscsiStorageId
import org.apache.sshd.client.session.ClientSession
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

object VBoxManage {


	private val mediatype = mapOf(
			DeviceType.disk to "hdd",
			DeviceType.cdrom to "dvddrive",
			DeviceType.floppy to "floppy"
	)

	fun startVm(session: ClientSession, vm : VirtualMachine, targetHost: Host, storageMap : Map<UUID, Triple<VirtualStorageDevice, VirtualStorageDeviceDynamic, Host>>) {
		session.executeOrDie("VBoxManage createvm --name ${vm.id} --uuid ${vm.id} --register")
		// memory is meant in megabytes, kerub has it in bytes, let's round it up
		session.executeOrDie("VBoxManage modifyvm --memory ${round(vm.memory.min)} ")
		session.executeOrDie("VBoxManage modifyvm --cpus ${vm.nrOfCpus} ")
		vm.virtualStorageLinks.forEach {
			link ->
			val storage = requireNotNull(storageMap[link.virtualStorageId])
			val device = storage.first
			val dyn = storage.second
			val host = storage.third
			session.executeOrDie(
					"VBoxManage storageattach ${vm.id} " +
							"--storagectl ${link.virtualStorageId} " +
							"--type ${mediatype[link.device]}" +
							"--medium ${medium(dyn, host, targetHost)}"
			)
		}
		session.executeOrDie("VBoxManage start ${vm.id}")
	}

	/**
	 * Round to MB
	 */
	internal fun round(amount: BigInteger) : String {
		val accurate = BigDecimal(amount).divide(MB)
		val round = accurate.toBigInteger()
		if(accurate > BigDecimal(round)) {
			return (round + BigInteger.ONE).toString()
		} else {
			return round.toString()
		}
	}

	private fun medium(dyn: VirtualStorageDeviceDynamic, storageHost: Host, targetHost : Host): String {
		if(targetHost.id == storageHost.id) {
			val allocation = dyn.allocation
			return when(allocation) {
				is VirtualStorageLvmAllocation -> { allocation.path }
				is VirtualStorageGvinumAllocation -> { "/dev/gvinum/${dyn.id}" }
				is VirtualStorageFsAllocation -> { "${allocation.mountPoint}/${dyn.id}" }
				else -> {
					TODO("Unknown allocation type: ${allocation}")
				}
			}
		} else {
			return "iscsi --server ${storageHost.address} --target ${iscsiStorageId(dyn.id) }"
		}
	}

	fun stopVm(session: ClientSession, vm : VirtualMachine) {
		session.executeOrDie("VBoxManage controlvm ${vm.id} poweroff")
	}
	fun createMedium(session: ClientSession, path : String, size : BigInteger, type: DeviceType, format: VirtualDiskFormat) {
		session.executeOrDie("VBoxManage create $type --filename $path --format $format")
	}

}

