package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.FireWall
import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.ServiceManager
import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

class OpenIndiana : Distribution {

	override val operatingSystem: OperatingSystem = OperatingSystem.Unix

	override fun getVersion(session: ClientSession): Version
			= Version.fromVersionString(session.executeOrDie("uname -r"))

	override fun name(): String = "openindiana"

	override fun handlesVersion(version: Version): Boolean = version.major == "5"

	override fun detect(session: ClientSession): Boolean = session.executeOrDie("uname -n") == name()

	override fun getPackageManager(session: ClientSession): PackageManager
			= TODO("https://github.com/kerubistan/kerub/issues/180")

	override fun installMonitorPackages(session: ClientSession) {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao) {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun getRequiredPackages(osCommand: OsCommand): List<String> {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun detectStorageCapabilities(session: ClientSession): List<StorageCapability> {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun detectHostCpuType(session: ClientSession): String = session.executeOrDie("uname -p")

	override fun getTotalMemory(session: ClientSession): BigInteger {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun getFireWall(session: ClientSession): FireWall {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun getServiceManager(session: ClientSession): ServiceManager {
		TODO("https://github.com/kerubistan/kerub/issues/180")
	}

	override fun getHostOs(): OperatingSystem = OperatingSystem.Unix
}