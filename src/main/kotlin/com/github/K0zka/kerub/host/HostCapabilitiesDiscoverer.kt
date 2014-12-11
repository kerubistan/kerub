package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.os.OperatingSystem
import org.apache.sshd.ClientSession
import com.github.K0zka.kerub.utils.version.Version
import com.github.K0zka.kerub.utils.SoftwarePackage
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.host.distros.Distribution
import com.github.K0zka.kerub.host.distros.Ubuntu
import com.github.K0zka.kerub.host.distros.Gentoo
import com.github.K0zka.kerub.host.distros.Fedora

/**
 * Helper class to detect host capabilities through an established SSH session.
 */
public object HostCapabilitiesDiscoverer {

	private val logger = getLogger(javaClass<HostCapabilitiesDiscoverer>())
	val distributions = listOf<Distribution>( Ubuntu(), Fedora(), Gentoo() )

	fun discoverHost(session: ClientSession) : HostCapabilities {

		val distro = detectDistro(session)
		return HostCapabilities(
				os = getHostOs(session),
				cpuArchitecture = getHostCpuType(session),
				distribution = getDistribution(session, distro) ,
				installedSoftware = distro?.listPackages(session) ?: listOf(),
		        totalMemory = getTotalMemory(session)
		                       )
	}

	fun getTotalMemory(session: ClientSession): Long {
		return session
				.execute("cat /proc/meminfo | grep  MemTotal")
				.substringAfter("MemTotal:")
				.substringBefore("kB")
				.trim()
				.toLong()
	}

	protected fun getHostOs(session: ClientSession): OperatingSystem {
		return OperatingSystem.valueOf(session.execute("uname -s").trim())
	}

	protected fun getHostKernelVersion(session: ClientSession): Version {
		return Version.fromVersionString(session.execute("uname -r").trim())
	}

	protected fun getHostCpuType(session: ClientSession): String {
		return session.execute("uname -p").trim()
	}

	protected fun detectDistro(session: ClientSession) : Distribution? {
		for (distro in distributions) {
			logger.debug("Checking host with ${distro.name()} distro helper")
			if (distro.detect(session)) {
				return distro
			}
		}
		return null
	}

	protected fun getDistribution(session: ClientSession, distro : Distribution?): SoftwarePackage? {
		if(distro == null) {
			return null
		} else {
			return SoftwarePackage(distro.name(), distro.getVersion(session))
		}
	}


}