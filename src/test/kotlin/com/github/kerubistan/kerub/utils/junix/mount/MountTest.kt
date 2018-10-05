package com.github.kerubistan.kerub.utils.junix.mount

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MountTest {

	private val session: ClientSession = mock()

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
		session.mockCommandExecution("mount.*".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/utils/junix/mount/mount-centos7.txt"))

		val mounts = Mount.listMounts(session)

		assertTrue(mounts.any {
			it.device == "/dev/vda2"
					&& it.type == "xfs"
					&& it.mountPoint == "/"
		})
	}

	@Test
	fun listMountsWithNetBSD() {
		session.mockCommandExecution("mount.*".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/utils/junix/mount/mount-netbsd7.txt"))

		val mounts = Mount.listMounts(session)

		assertTrue(mounts.any {
			it.device == "/dev/wd0a"
					&& it.type == "ffs"
					&& it.mountPoint == "/"
		})
	}
}