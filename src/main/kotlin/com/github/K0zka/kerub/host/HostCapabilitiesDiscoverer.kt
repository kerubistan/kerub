package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.host.distros.*
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.hardware.ChassisInformation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.hardware.SystemInformation
import com.github.K0zka.kerub.os.OperatingSystem
import com.github.K0zka.kerub.utils.SoftwarePackage
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.dmi.DmiDecoder
import com.github.K0zka.kerub.utils.junix.lspci.LsPci
import com.github.K0zka.kerub.utils.version.Version
import org.apache.sshd.ClientSession
import kotlin.reflect.KClass
import kotlin.reflect.jvm.java
import kotlin.reflect.jvm.kotlin

/**
 * Helper class to detect host capabilities through an established SSH session.
 */
public object HostCapabilitiesDiscoverer {

	private val logger = getLogger(HostCapabilitiesDiscoverer::class)
	internal val distributions = listOf<Distribution>(Ubuntu(), Fedora(), Centos6())

	internal fun <T : Any> valuesOfType(list: Collection<*>, clazz: KClass<T>): List<T> {
		return list.filter { it?.javaClass?.kotlin == clazz }.map { clazz.java.cast(it) }
	}

	fun discoverHost(session: ClientSession, dedicated : Boolean = false): HostCapabilities {

		val distro = detectDistro(session)
		val packages = distro?.listPackages(session) ?: listOf()
		val dmiDecodeInstalled = installDmi(dedicated, distro, packages, session)
		val systemInfo = if (dmiDecodeInstalled) DmiDecoder.parse(runDmiDecode(session)) else mapOf()

		val hardwareInfo = systemInfo.values()
		return HostCapabilities(
				os = getHostOs(session),
				cpuArchitecture = getHostCpuType(session),
				distribution = getDistribution(session, distro),
				installedSoftware = packages,
				totalMemory = getTotalMemory(session),
				system = valuesOfType(hardwareInfo, SystemInformation::class).firstOrNull(),
				cpus = valuesOfType(hardwareInfo, ProcessorInformation::class),
				chassis = valuesOfType(hardwareInfo, ChassisInformation::class).firstOrNull(),
				devices = LsPci.execute(session)
		                       )
	}

	internal fun installDmi(dedicated: Boolean, distro: Distribution?, packages: List<SoftwarePackage>, session: ClientSession): Boolean {
		val dmiDecodeInstalled = isDmiDecodeInstalled(packages)
		if (!dmiDecodeInstalled && dedicated && distro != null) {
			distro.installPackage("dmidecode", session)
			return true
		}
		return dmiDecodeInstalled
	}

	internal fun runDmiDecode(session: ClientSession): String =
			session.execute("dmidecode")

	internal fun isDmiDecodeInstalled(packages: List<SoftwarePackage>): Boolean {
		return packages.any { "dmidecode" == it.name }
	}

	fun getTotalMemory(session: ClientSession): Long {
		return session
				.execute("cat /proc/meminfo | grep  MemTotal")
				.substringAfter("MemTotal:")
				.substringBefore("kB")
				.trim()
				.toLong()
	}

	internal fun getHostOs(session: ClientSession): OperatingSystem {
		return OperatingSystem.valueOf(session.execute("uname -s").trim())
	}

	internal fun getHostKernelVersion(session: ClientSession): Version {
		return Version.fromVersionString(session.execute("uname -r").trim())
	}

	internal fun getHostCpuType(session: ClientSession): String {
		return session.execute("uname -p").trim()
	}

	internal fun detectDistro(session: ClientSession): Distribution? {
		for (distro in distributions) {
			logger.debug("Checking host with ${distro.name()} distro helper")
			if (distro.detect(session)) {
				return distro
			}
		}
		return null
	}

	internal fun getDistribution(session: ClientSession, distro: Distribution?): SoftwarePackage? {
		if (distro == null) {
			return null
		} else {
			return SoftwarePackage(distro.name(), distro.getVersion(session))
		}
	}


}