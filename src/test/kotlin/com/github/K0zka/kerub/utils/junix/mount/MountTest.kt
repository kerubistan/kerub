package com.github.K0zka.kerub.utils.junix.mount

import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.testHostCapabilities
import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MountTest : AbstractJunixCommandVerification() {
	@Test
	fun available() {
		assertTrue(Mount.available(testHostCapabilities.copy(os = OperatingSystem.Linux)))
		assertTrue(Mount.available(testHostCapabilities.copy(
				os = OperatingSystem.BSD,
				distribution = SoftwarePackage(name = "NetBSD", version = Version.fromVersionString("7"))
		)))
		assertFalse(Mount.available(testHostCapabilities.copy(os = OperatingSystem.Windows)))
	}

	@Test
	fun listMountsWithCentos7() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { resource("com/github/K0zka/kerub/utils/junix/mount/mount-centos7.txt") }

		val mounts = Mount.listMounts(session)

		assertTrue(mounts.any {
			it.device == "/dev/vda2"
					&& it.type == "xfs"
					&& it.mountPoint == "/"
		})
	}

	@Test
	fun listMountsWithNetBSD() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { resource("com/github/K0zka/kerub/utils/junix/mount/mount-netbsd7.txt") }

		val mounts = Mount.listMounts(session)

		assertTrue(mounts.any {
			it.device == "/dev/wd0a"
					&& it.type == "ffs"
					&& it.mountPoint == "/"
		})
	}

}