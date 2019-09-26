package com.github.kerubistan.kerub.utils.junix.uname

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UNameTest {

	private val session = mock<ClientSession>()

	@Test
	fun available() {
		assertTrue(UName.available(null))
	}

	@Test
	fun kernelName() {
		session.mockCommandExecution("uname -s".toRegex(), output = "TEST")
		assertEquals("TEST", UName.kernelName(session))
	}

	@Test
	fun machineType() {
		session.mockCommandExecution("uname -m".toRegex(), output = "TEST")
		assertEquals("TEST", UName.machineType(session))
	}

	@Test
	fun kernelVersion() {
		session.mockCommandExecution("uname -r".toRegex(), output = "1.2.3")
		assertEquals("1.2.3", UName.kernelVersion(session))
	}

	@Test
	fun operatingSystem() {
		session.mockCommandExecution("uname -o".toRegex(), output = "TestOS")
		assertEquals("TestOS", UName.operatingSystem(session))
	}

	@Test
	fun processorType() {
		session.mockCommandExecution("uname -p".toRegex(), output = "test-cpu")
		assertEquals("test-cpu", UName.processorType(session))
	}
}