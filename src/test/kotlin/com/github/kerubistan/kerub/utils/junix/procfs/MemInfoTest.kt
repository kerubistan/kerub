package com.github.kerubistan.kerub.utils.junix.procfs

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resource
import io.github.kerubistan.kroki.size.KB
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Test

class MemInfoTest {

	private val session: ClientSession = mock()
	private val sftpClient: SftpClient = mock()

	@Test
	fun totalWithCygwin() {
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).then {
			resource("com/github/kerubistan/kerub/utils/junix/procfs/cygwin-meminfo.txt")
		}
		assertEquals(4193780.KB, MemInfo.total(session))
	}

	@Test
	fun totalWithLinux() {
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).then {
			resource("com/github/kerubistan/kerub/utils/junix/procfs/linux-meminfo.txt")
		}
		assertEquals(16345292.KB, MemInfo.total(session))
	}

}