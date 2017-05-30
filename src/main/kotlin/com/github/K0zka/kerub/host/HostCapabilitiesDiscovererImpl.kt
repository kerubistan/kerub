package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.exc.UnknownHostOperatingSystemException
import com.github.K0zka.kerub.host.distros.Centos6
import com.github.K0zka.kerub.host.distros.Centos7
import com.github.K0zka.kerub.host.distros.Cygwin
import com.github.K0zka.kerub.host.distros.Debian
import com.github.K0zka.kerub.host.distros.Distribution
import com.github.K0zka.kerub.host.distros.Fedora
import com.github.K0zka.kerub.host.distros.Fedora23Plus
import com.github.K0zka.kerub.host.distros.FreeBSD
import com.github.K0zka.kerub.host.distros.Gentoo
import com.github.K0zka.kerub.host.distros.OpenIndiana
import com.github.K0zka.kerub.host.distros.OpenSuse
import com.github.K0zka.kerub.host.distros.Raspbian
import com.github.K0zka.kerub.host.distros.Ubuntu
import com.github.K0zka.kerub.host.distros.UbuntuBSD
import com.github.K0zka.kerub.host.distros.XenServer7
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.hardware.ChassisInformation
import com.github.K0zka.kerub.model.hardware.MemoryInformation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.hardware.SystemInformation
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.dmi.DmiDecoder
import com.github.K0zka.kerub.utils.junix.lspci.LsPci
import com.github.K0zka.kerub.utils.junix.virt.virsh.Virsh
import com.github.K0zka.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession
import kotlin.reflect.KClass

/**
 * Helper class to detect host capabilities through an established SSH session.
 */
class HostCapabilitiesDiscovererImpl : HostCapabilitiesDiscoverer {

	private companion object {
		val logger = getLogger(HostCapabilitiesDiscovererImpl::class)
		internal val distributions = listOf(
				//RPM-based distros
				Fedora(),
				Fedora23Plus(),
				Gentoo(),
				Centos6(),
				Centos7(),
				OpenSuse(),
				XenServer7(),
				// Debian-family
				Debian(),
				Ubuntu(),
				Raspbian(),
				// The BSD's
				FreeBSD(),
				UbuntuBSD(),
				//'UNIX'es
				OpenIndiana(),
				//Windows
				Cygwin())
	}

	fun <T : Any> valuesOfType(list: Collection<*>, clazz: KClass<T>): List<T> {
		return list.filter { it?.javaClass?.kotlin == clazz }.map { clazz.java.cast(it) }
	}

	override
	fun discoverHost(session: ClientSession, dedicated: Boolean): HostCapabilities {

		val distro = detectDistro(session)
		val packages = silent { distro.getPackageManager(session).list() } ?: listOf<SoftwarePackage>()
		val dmiDecodeInstalled = installDmi(dedicated, distro, packages, session)
		val systemInfo = if (dmiDecodeInstalled) DmiDecoder.parse(runDmiDecode(session)) else mapOf()

		val osDetectedFlags = distro.detectHostCpuFlags(session)
		val hardwareInfo = systemInfo.values
		val hostOs = distro.getHostOs()
		val distribution = SoftwarePackage(distro.name(), distro.getVersion(session))

		val hypervisorCapabilities =
				if (Virsh.available(distribution, packages))
					silent { listOf(Virsh.capabilities(session)) } ?: listOf<Any>()
				else
					listOf<Any>()

		return HostCapabilities(
				os = hostOs,
				cpuArchitecture = distro.detectHostCpuType(session),
				distribution = distribution,
				installedSoftware = packages,
				totalMemory = distro.getTotalMemory(session),
				memoryDevices = valuesOfType(hardwareInfo, MemoryInformation::class),
				system = valuesOfType(hardwareInfo, SystemInformation::class).firstOrNull(),
				cpus = valuesOfType(hardwareInfo, ProcessorInformation::class).map {
					it.copy(
							flags = (it.flags + osDetectedFlags).toSet().toList()
					)
				},
				chassis = valuesOfType(hardwareInfo, ChassisInformation::class).firstOrNull(),
				devices = LsPci.execute(session),
				powerManagment = distro.detectPowerManagement(session),
				storageCapabilities = distro.detectStorageCapabilities(session, distribution, packages),
				hypervisorCapabilities = hypervisorCapabilities
		)
	}

	internal fun installDmi(dedicated: Boolean, distro: Distribution?, packages: List<SoftwarePackage>, session: ClientSession): Boolean {
		val dmiDecodeInstalled = isDmiDecodeInstalled(packages)
		if (!dmiDecodeInstalled && dedicated && distro != null) {
			distro.getPackageManager(session).install("dmidecode")
			return true
		}
		return dmiDecodeInstalled
	}

	fun runDmiDecode(session: ClientSession): String =
			session.execute("dmidecode")

	fun isDmiDecodeInstalled(packages: List<SoftwarePackage>): Boolean {
		return packages.any { "dmidecode" == it.name }
	}

	fun getHostKernelVersion(session: ClientSession): Version {
		return Version.fromVersionString(session.execute("uname -r").trim())
	}

	override fun detectDistro(session: ClientSession): Distribution {
		for (distro in distributions) {
			logger.debug("Checking host with ${distro.name()} distro helper")
			if (distro.detect(session)) {
				return distro
			}
		}
		throw UnknownHostOperatingSystemException("Mone of the distributions matched: "
				+ distributions.map { "${it.operatingSystem}/${it.name()}" }.joinToString(","))
	}


}