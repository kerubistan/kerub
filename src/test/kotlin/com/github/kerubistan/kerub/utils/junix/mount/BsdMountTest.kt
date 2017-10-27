package com.github.kerubistan.kerub.utils.junix.mount

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.junit.Assert.assertTrue
import org.junit.Test

class BsdMountTest : AbstractJunixCommandVerification() {

	@Test
	fun available() {
		assertTrue(BsdMount.available(testHostCapabilities.copy(
				os = OperatingSystem.BSD,
				distribution = SoftwarePackage(name = "FreeBSD", version = Version.fromVersionString("11"))
				)))
	}

	@Test
	fun listMountsWithNetbsd7() {
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { resource("com/github/kerubistan/kerub/utils/junix/mount/mount-freebsd11.txt") }

		val list = BsdMount.listMounts(session)

		assertTrue(list.any {
			it.device == "zroot/ROOT/default"
					&& it.mountPoint == "/"
					&& it.type == "zfs"
					&& it.options == listOf("local", "noatime", "nfsv4acls")
		})
	}

}