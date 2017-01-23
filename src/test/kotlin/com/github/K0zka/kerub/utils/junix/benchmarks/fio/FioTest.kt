package com.github.K0zka.kerub.utils.junix.benchmarks.fio

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.testHostCapabilities
import com.github.K0zka.kerub.utils.junix.AbstractJunixCommandVerification
import com.github.K0zka.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FioTest : AbstractJunixCommandVerification() {

	val sftpClient: SftpClient = mock()

	@Test
	fun benchmarkIoDevice() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/benchmarks/fio/sample-output.json")
		}
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.write(any())).then { ByteArrayOutputStream() }

		val results = Fio.benchmarkIoDevice(session, "/dev/test")

		val rand = results.jobs.first { it.jobName == "rand" }
	}

	@Test
	fun available() {
		assertTrue {
			Fio.available(testHostCapabilities.copy(
					installedSoftware = listOf(
							SoftwarePackage("fio", Version.fromVersionString("2.2.10"))
					)
			))
		}
		assertFalse {
			Fio.available(testHostCapabilities.copy(
					installedSoftware = listOf()
			))
		}
	}

}