package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.Centos
import com.github.kerubistan.kerub.utils.junix.common.openSuse
import com.nhaarman.mockito_kotlin.mock
import io.github.kerubistan.kroki.size.GB
import org.apache.sshd.client.session.ClientSession
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LvmThinLvTest {

	val session: ClientSession = mock()

	@Test
	fun available() {
		assertFalse("lvm thin is not available on any unix-like thing") {
			LvmThinLv.available(null)
		}
		assertFalse("lvm2 is not enough unfortunately") {
			LvmThinLv.available(
					testHostCapabilities.copy(
							installedSoftware = listOf(
									SoftwarePackage.pack("lvm2", "1.2.3")
							)
					)
			)
		}
		assertTrue("thin is ok with device-mapper installed on opensuse") {
			LvmThinLv.available(
					testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							distribution = SoftwarePackage.pack(openSuse, version = "1.2.3"),
							installedSoftware = listOf(
									SoftwarePackage.pack("lvm2", "1,2,3"),
									SoftwarePackage.pack("device-mapper", "1,2,3")
							)
					)
			)
		}

		assertTrue("thin is ok  with thin-provisioning-tools installled  on opensuse") {
			LvmThinLv.available(
					testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							distribution = SoftwarePackage.pack(openSuse, version = "1.2.3"),
							installedSoftware = listOf(
									SoftwarePackage.pack("lvm2", "1,2,3"),
									SoftwarePackage.pack("thin-provisioning-tools", "1,2,3")
							)
					)
			)
		}

		assertTrue("thin is ok with device-mapper-persistent-data installed on rh-like things") {
			LvmThinLv.available(
					testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							distribution = SoftwarePackage.pack(Centos, version = "7.5"),
							installedSoftware = listOf(
									SoftwarePackage.pack("lvm2", "1,2,3"),
									SoftwarePackage.pack("device-mapper-persistent-data", "1,2,3")
							)
					)
			)
		}

		assertFalse("thin is not ok without device-mapper-persistent-data installed on rh-like things") {
			LvmThinLv.available(
					testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							distribution = SoftwarePackage.pack(Centos, version = "7.5"),
							installedSoftware = listOf(
									SoftwarePackage.pack("lvm2", "1,2,3")
							)
					)
			)
		}

	}

	@Test
	fun createPool() {
		session.mockCommandExecution("lvm lvcreate .*".toRegex(), output = "  Logical volume \"pool0\" created.\n")
		LvmThinLv.createPool(
				session = session,
				name = "pool0",
				metaSize = 1.GB,
				size = 200.GB,
				vgName = "pool"
		)
		session.verifyCommandExecution("lvm lvcreate .*".toRegex())
	}


}