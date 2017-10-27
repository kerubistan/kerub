package com.github.kerubistan.kerub.utils.junix.iscsi.ctld

import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CtldTest {

	@Test
	fun available() {
		assertFalse("no capabilities known, no ctld", Ctld.available(null))
		assertFalse("ctld only on bsd",
				Ctld.available(
						HostCapabilities(
								os = OperatingSystem.Linux,
								distribution = null,
								cpuArchitecture = "X86_64",
								totalMemory = "8 GB".toSize()
						)
				)
		)
		assertFalse("ctld only on freebsd",
				Ctld.available(
						HostCapabilities(
								os = OperatingSystem.BSD,
								distribution = null,
								cpuArchitecture = "X86_64",
								totalMemory = "8 GB".toSize()
						)
				)
		)
		assertTrue("all freebsd have ctld",
				Ctld.available(
						HostCapabilities(
								os = OperatingSystem.BSD,
								distribution = SoftwarePackage(
										name = "FreeBSD",
										version = Version.fromVersionString("10.0")
								),
								cpuArchitecture = "X86_64",
								totalMemory = "8 GB".toSize()
						)
				)
		)
	}

}