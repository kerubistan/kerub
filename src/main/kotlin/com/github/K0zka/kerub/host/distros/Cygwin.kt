package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.FireWall
import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.ServiceManager
import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.host.packman.CygwinPackageManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.procfs.MemInfo
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

class Cygwin : Distribution {
	override fun getVersion(session: ClientSession) =
			Version.fromVersionString(session.executeOrDie("uname -r").substringBefore("("))

	override fun name(): String =
			"Cygwin"

	override fun handlesVersion(version: Version): Boolean
		= version.major.toInt() >= 2

	override fun detect(session: ClientSession): Boolean
			= session.executeOrDie("uname -o").trim() == "Cygwin"

	override fun getPackageManager(session: ClientSession): PackageManager = CygwinPackageManager(session)

	override fun installMonitorPackages(session: ClientSession) {
		TODO()
	}

	override fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao) {
		TODO()
	}

	override fun getRequiredPackages(osCommand: OsCommand): List<String> = listOf()

	override fun detectStorageCapabilities(session: ClientSession): List<StorageCapability> = listOf()

	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> = listOf() // TODO

	override fun detectHostCpuType(session: ClientSession): String = session.executeOrDie("uname -p").toUpperCase()

	override fun getTotalMemory(session: ClientSession): BigInteger =
		MemInfo.total(session)

	override fun getFireWall(session: ClientSession): FireWall {
		TODO()
	}

	override fun getServiceManager(session: ClientSession): ServiceManager {
		TODO()
	}

	override fun getHostOs(): OperatingSystem = operatingSystem

	override val operatingSystem = OperatingSystem.Windows
}
