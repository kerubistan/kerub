package com.github.K0zka.kerub.utils.junix.procfs

import com.github.K0zka.kerub.utils.resource
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Test

class MemInfoTest {

	val session : ClientSession = mock()
	val sftpClient : SftpClient = mock()

	@Test
	fun totalWithCygwin() {
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).then {
			resource("com/github/K0zka/kerub/utils/junix/procfs/cygwin-meminfo.txt")
		}
		assertEquals("4193780 kB".toSize(), MemInfo.total(session))
	}

	@Test
	fun totalWithLinux() {
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).then {
			resource("com/github/K0zka/kerub/utils/junix/procfs/linux-meminfo.txt")
		}
		assertEquals("16345292 kB".toSize(), MemInfo.total(session))
	}

}