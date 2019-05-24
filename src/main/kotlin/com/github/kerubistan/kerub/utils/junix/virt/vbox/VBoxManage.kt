package com.github.kerubistan.kerub.utils.junix.virt.vbox

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.MB
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.silent
import com.github.kerubistan.kerub.utils.storage.iscsiStorageId
import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.time.now
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.math.BigInteger.ZERO
import java.util.UUID

object VBoxManage : OsCommand {

	private val knownPackages = listOf("virtualbox", "virtualbox-ose")

	private val supportedFormats = setOf(VirtualDiskFormat.vdi, VirtualDiskFormat.vmdk)

	override fun available(hostCapabilities: HostCapabilities?)
			= hostCapabilities?.installedSoftware?.any { knownPackages.contains(it.name.toLowerCase()) } == true

	private val mediatype = mapOf(
			DeviceType.disk to "hdd",
			DeviceType.cdrom to "dvddrive",
			DeviceType.floppy to "floppy"
	)

	fun startVm(session: ClientSession, vm: VirtualMachine, targetHost: Host, storageMap: Map<UUID, Triple<VirtualStorageDevice, VirtualStorageDeviceDynamic, Host>>) {
		var success = false
		try {
			session.executeOrDie("VBoxManage createvm --name ${vm.id} --uuid ${vm.id} --register")
			// memory is meant in megabytes, kerub has it in bytes, let's round it up
			session.executeOrDie("VBoxManage modifyvm ${vm.id} --memory ${round(vm.memory.min)} ")
			session.executeOrDie("VBoxManage modifyvm ${vm.id} --cpus ${vm.nrOfCpus} ")
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
			session.executeOrDie("VBoxManage startvm ${vm.id} --type headless")
			success = true
		} finally {
			if (!success) {
				silent(level = LogLevel.Warning) {
					session.executeOrDie("VBoxManage unregistervm ${vm.id} --delete")
				}
			}
		}
	}

	private class VBoxMonitorInputStream(private val callback: (ts: Long, metrics: VBoxMetrics) -> Unit) : OutputStream() {
		companion object {
			private val spaces = "\\s+".toRegex()
		}

		private val buffer = StringBuilder()
		override fun write(data: ByteArray?) {
			buffer.append(data?.toString(Charsets.US_ASCII))
			tryEval()
		}

		override fun write(data: Int) {
			buffer.append(data.toChar())
			tryEval()
		}

		private fun tryEval() {

			fun String.percent() = this.substringBefore("%").trim().toFloat()

			fun <T> Map<String, String>.metric(prefix: String, transform: (String?) -> T) =
					VBoxMetric(
							now = this[prefix].let(transform),
							max = this["$prefix:max"].let(transform),
							min = this["$prefix:min"].let(transform),
							avg = this["$prefix:avg"].let(transform)
					)

			fun netRate(input: String?): Int
					= input?.substringBefore("/s")?.toSize()?.toInt() ?: 0

			fun size(input: String?): BigInteger = input?.toSize() ?: ZERO
			fun percent(input: String?): Float = input?.percent() ?: 0.toFloat()

			if (buffer.endsWith("---------\n")) {
				val report = buffer.lines()
						.filterNot { it.startsWith("--") || it.startsWith("Time") || it.isEmpty() }
						.map {
							it.split(spaces).let { it[2] to it.subList(3, it.size).joinToString(" ") }
						}.toMap()
				if (report.isNotEmpty()) {
					val metrics = VBoxMetrics(
							userCpu = report.metric("CPU/Load/User", ::percent),
							kernelCpu = report.metric("CPU/Load/Kernel", ::percent),
							ramUsed = report.metric("RAM/Usage/Used", ::size),
							netRateRx = report.metric("Net/Rate/Rx", ::netRate),
							netRateTx = report.metric("Net/Rate/Tx", ::netRate),
							diskUsed = report.metric("Disk/Usage/Used", ::size)
					)
					callback(now(), metrics)
				}
			}
		}
	}

	fun monitorVm(session: ClientSession, vmName: String, callback: (ts: Long, metrics: VBoxMetrics) -> Unit) {
		val channel = session.createExecChannel("VBoxManage metrics collect $vmName")
		channel.out = VBoxMonitorInputStream(callback)
		channel.open().await()
	}

	/**
	 * Round to MB
	 */
	internal fun round(amount: BigInteger): String {
		val accurate = BigDecimal(amount).divide(MB)
		val round = accurate.toBigInteger()
		return if (accurate > BigDecimal(round)) {
			(round + BigInteger.ONE).toString()
		} else {
			round.toString()
		}
	}

	private fun medium(dyn: VirtualStorageDeviceDynamic, storageHost: Host, targetHost: Host): String {
		if (targetHost.id == storageHost.id) {
			val allocation = dyn.allocation
			return when (allocation) {
				is VirtualStorageLvmAllocation -> {
					allocation.path
				}
				is VirtualStorageGvinumAllocation -> {
					"/dev/gvinum/${dyn.id}"
				}
				is VirtualStorageFsAllocation -> {
					"${allocation.mountPoint}/${dyn.id}"
				}
				else -> {
					TODO("Unknown allocation type: $allocation")
				}
			}
		} else {
			return "iscsi --server ${storageHost.address} --target ${iscsiStorageId(dyn.id)}"
		}
	}

	fun stopVm(session: ClientSession, vm: VirtualMachine) {
		controlVm(session, vm, "poweroff")
	}

	private fun controlVm(session: ClientSession,
						  vm: VirtualMachine,
						  command : String,
						  args : String? = null) =
		session.executeOrDie("VBoxManage controlvm ${vm.id} $command ${args ?: ""}")

	fun pauseVm(session: ClientSession, vm: VirtualMachine) =
		controlVm(session, vm, "pause")

	fun resumeVm(session: ClientSession, vm: VirtualMachine) =
		controlVm(session, vm, "resume")

	fun resetVm(session: ClientSession, vm: VirtualMachine) =
		controlVm(session, vm, "reset")

	fun teleportVm(session: ClientSession, vm: VirtualMachine, address: String, port: Int, password: String,
				   listener: (Int) -> Unit) =
			controlVm(session, vm, "teleport", "--host $address --port $port --password $password")

	private fun vboxType(type: DeviceType) = when(type) {
		DeviceType.cdrom -> "dvd"
		else -> type.name
	}

	fun createMedium(session: ClientSession,
					 path: String,
					 size: BigInteger,
					 type: DeviceType,
					 format: VirtualDiskFormat = VirtualDiskFormat.vdi) {
		check(size > ZERO) {
			"medium size ($size) must be greater than 0"
		}
		check(format in supportedFormats) {
			"format ($format) not supported, only $supportedFormats"
		}
		session.executeOrDie("VBoxManage createmedium ${vboxType(type)} --filename $path --format $format --sizebyte ${round(size)}")
	}

}

