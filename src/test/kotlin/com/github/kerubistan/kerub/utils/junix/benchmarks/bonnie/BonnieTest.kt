package com.github.kerubistan.kerub.utils.junix.benchmarks.bonnie

import com.github.kerubistan.kerub.KB
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.resourceToString
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
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

	@Test
	fun run() {
		session.mockCommandExecution(
				commandMatcher = "bonnie.*".toRegex(),
				output = resourceToString(
						"com/github/kerubistan/kerub/utils/junix/benchmarks/bonnie/bonnie_laptop_sata.txt"
				)
		)
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
	fun runWithSdcard() {
		session.mockCommandExecution(
				commandMatcher = "bonnie.*".toRegex(),
				output = resourceToString(
						"com/github/kerubistan/kerub/utils/junix/benchmarks/bonnie/bonnie_sdcard_espressobin.txt"
				)
		)
		val data = Bonnie.run(session = session, directory = "/tmp")
		assertEquals(
				IoBenchmarkItem(throughput = 124.KB, cpuUsagePercent = 99, latency = 112000),
				data.sequentialOutputPerChr
		)
		assertEquals(
				IoBenchmarkItem(throughput = 25807.KB, cpuUsagePercent =14, latency = 668000),
				data.sequentialOutputPerBlock
		)
	}

	@Test
	fun runSsdSata() {
		session.mockCommandExecution(
				commandMatcher = "bonnie.*".toRegex(),
				output = resourceToString(
						"com/github/kerubistan/kerub/utils/junix/benchmarks/bonnie/bonnie_nuc_ssd_sata.txt"
				)
		)
		Bonnie.run(session = session, directory = "/tmp")
	}

	@Test
	@Ignore("sample to be updated")
	fun runSsdPcie() {
		session.mockCommandExecution(
				commandMatcher = "bonnie.*".toRegex(),
				output = resourceToString(
						"com/github/kerubistan/kerub/utils/junix/benchmarks/bonnie/bonnie_ssd_mpcie.txt"
				)
		)
		Bonnie.run(session = session, directory = "/tmp")
	}

}