package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.FireWall
import com.github.K0zka.kerub.host.ServiceManager
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.host.fw.IpfwFireWall
import com.github.K0zka.kerub.host.packman.PkgPackageManager
import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.storagemanager.gvinum.GVinum
import com.github.K0zka.kerub.utils.toBigInteger
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

/**
 * FreeBSD distribution.
 * Some settings need to be modified in sshd configuration:
 * PermitRootLogin yes
 */
class FreeBSD : Distribution {

	override fun getTotalMemory(session: ClientSession) : BigInteger {
		return (session
				.executeOrDie("sysctl hw.physmem")
				.substringAfter("hw.physmem:").trim())
				.toBigInteger()
	}

	override fun detectStorageCapabilities(session: ClientSession): List<StorageCapability> {
		return GVinum.listDrives(session).map {
			drive ->
			GvinumStorageCapability(name = drive.name, device = drive.device, size = drive.size)
		}
	}

	override fun getServiceManager(session: ClientSession): ServiceManager {
		TODO("issue #57")
	}

	override fun installMonitorPackages(session: ClientSession) {
		//TODO issue #57
	}

	override fun getRequiredPackages(utility: OsCommand): List<String> {
		//TODO issue #57
		return listOf()
	}

	override val operatingSystem = OperatingSystem.BSD

	override fun getVersion(session: ClientSession): Version = Version.fromVersionString(session.execute("uname -r"))

	override fun name(): String = "FreeBSD"

	override fun handlesVersion(version: Version): Boolean = version.major >= "10"

	override fun detect(session: ClientSession): Boolean = session.execute("uname -s").trim() == "FreeBSD"

	override fun getPackageManager(session: ClientSession) = PkgPackageManager(session)

	override fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao) {
		//TODO issue #57
	}

	override fun getFireWall(session: ClientSession): FireWall = IpfwFireWall(session)

	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> {
		return listOf()
	}

	//BSD distributions have different naming conventions for architectures
	val cpuTypeMap = mapOf(
			"amd64" to "X86_64"
	)

	override fun detectHostCpuType(session: ClientSession): String {
		val cpuType =  cpuTypeByOS(session)
		return cpuTypeMap[cpuType] ?: cpuType
	}

	private fun cpuTypeByOS(session: ClientSession): String {
		val processorType = session.execute("uname -p").trim()
		if (processorType == "unknown") {
			return session.execute("uname -m").trim()
		} else {
			return processorType
		}
	}

}