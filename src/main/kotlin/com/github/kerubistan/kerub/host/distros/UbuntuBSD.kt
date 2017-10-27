package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.doWithDyn
import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.packman.RaspbianPackageManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.utils.junix.df.DF
import com.github.kerubistan.kerub.utils.junix.mount.BsdMount
import com.github.kerubistan.kerub.utils.junix.vmstat.VmStat
import com.github.kerubistan.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession

class UbuntuBSD : AbstractDebian("ubuntuBSD") {
	override fun handlesVersion(version: Version): Boolean =
			silent { version.major.toInt() >= 16 } ?: false

	// TODO https://github.com/kerubistan/kerub/issues/170
	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> = listOf()

	//the output format of dpkg is also not conventional ubuntu
	override fun getPackageManager(session: ClientSession): PackageManager
			= RaspbianPackageManager(session)

	//only ZFS "volume manager" in ubuntuBSD https://github.com/kerubistan/kerub/issues/174
	override fun detectStorageCapabilities(session: ClientSession, osVersion: SoftwarePackage, packages: List<SoftwarePackage>): List<StorageCapability> =
			joinMountsAndDF(DF.df(session), BsdMount.listMounts(session))

	override fun installMonitorPackages(session: ClientSession) {
		//TODO: find monitoring packages
	}

	override fun startMonitorProcesses(
			session: ClientSession,
			host: Host,
			hostDynDao: HostDynamicDao) {
		VmStat.vmstat(session, { event ->
			hostDynDao.doWithDyn(host.id) {
				val memFree = (event.freeMem
						+ event.cacheMem
						+ event.ioBuffMem)
				it.copy(
						status = HostStatus.Up,
						idleCpu = event.idleCpu,
						systemCpu = event.systemCpu,
						userCpu = event.userCpu,
						memFree = memFree,
						memUsed = host.capabilities?.totalMemory?.minus(memFree),
						memSwapped = event.swapMem
				)
			}
		})
	}
}