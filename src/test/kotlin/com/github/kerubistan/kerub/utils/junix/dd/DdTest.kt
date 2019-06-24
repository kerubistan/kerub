package com.github.kerubistan.kerub.utils.junix.dd

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertTrue
import org.junit.Test

class DdTest {

	@Test
	fun available() {
		assertTrue(Dd.available(null))
	}

	@Test
	fun copy() {
		val session = mock<ClientSession>()
		session.mockCommandExecution("dd .*".toRegex())
		Dd.copy(session, "/dev/source", "/dev/target")

		session.verifyCommandExecution("dd .*".toRegex())
	}
}