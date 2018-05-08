package com.github.kerubistan.kerub.utils.junix.benchmarks.bonnie

import com.github.kerubistan.kerub.KB
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BonnieTest {

	@Test
	fun available() {
		assertFalse("no data - not bonnie") {
			Bonnie.available(null)
		}
		assertFalse("no bonnie - no bonnie") {
			Bonnie.available(testHostCapabilities.copy(
					installedSoftware = listOf()
			))
		}
		assertTrue("have bonnie - have bonnie") {
			Bonnie.available(testHostCapabilities.copy(
					distribution = SoftwarePackage("Kotsog Linux", version = Version.fromVersionString("1.2.3")),
					installedSoftware = listOf(
							SoftwarePackage(name = "bonnie++", version = Version.fromVersionString("1.97"))
					)
			))
		}
	}

	private val session = mock<ClientSession>()
	private val exec = mock<ChannelExec>()
	private val future = mock<OpenFuture>()

	@Before
	fun setup() {
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).then { NullInputStream(0) }
	}

	@Test
	fun run() {
		whenever(exec.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/benchmarks/bonnie/bonnie_laptop_sata.txt")
		}
		val data = Bonnie.run(session = session, directory = "/tmp")
		assertEquals(
				IoBenchmarkItem(throughput = 831.KB, cpuUsagePercent = 97, latency = 25012),
				data.sequentialOutputPerChr
		)
		assertEquals(
				IoBenchmarkItem(throughput = 106328.KB, cpuUsagePercent = 6, latency = 1340000),
				data.sequentialOutputPerBlock
		)
		assertEquals(
				IoBenchmarkItem(throughput = 49281.KB, cpuUsagePercent = 4, latency = 5440000),
				data.sequentialOutputRewrite
		)
		assertEquals(
				IoBenchmarkItem(throughput = 2055.KB, cpuUsagePercent = 95, latency = 41526),
				data.sequentialInputPerChr
		)
		assertEquals(
				IoBenchmarkItem(throughput = 117861.KB, cpuUsagePercent = 5, latency = 288000),
				data.sequentialInputPerBlock
		)
	}

	@Test
	fun runSsdSata() {
		whenever(exec.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/benchmarks/bonnie/bonnie_nuc_ssd_sata.txt")
		}
		Bonnie.run(session = session, directory = "/tmp")
	}

	@Test
	@Ignore("sample to be updated")
	fun runSsdPcie() {
		whenever(exec.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/benchmarks/bonnie/bonnie_ssd_mpcie.txt")
		}
		Bonnie.run(session = session, directory = "/tmp")
	}

}