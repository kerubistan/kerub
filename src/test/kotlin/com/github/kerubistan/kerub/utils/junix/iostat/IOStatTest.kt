package com.github.kerubistan.kerub.utils.junix.iostat

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.Ubuntu
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.github.kerubistan.kroki.io.resourceToString
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertTrue

class IOStatTest {


	@Test
	fun available() {
		assertTrue("iostat on ubuntu") {
			IOStat.available(
					testHostCapabilities.copy(
							os = OperatingSystem.Linux,
							distribution = pack(Ubuntu, "18.04"),
							installedSoftware = listOf(
									pack("sysstat", "11.1.2")
							)
					))
		}
	}

	@Test
	fun monitor() {
		val session = mock<ClientSession>()
		val listener = mock<(List<IOStatEvent>) -> Unit>()
		session.mockProcess(
				"iostat.*".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/utils/junix/iostat/iostat.txt"))
		IOStat.monitor(session, listener)

		verify(listener, times(60)).invoke(any())
	}
}