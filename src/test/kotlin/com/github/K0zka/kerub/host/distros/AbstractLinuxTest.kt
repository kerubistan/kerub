package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.lom.WakeOnLanInfo
import com.github.K0zka.kerub.utils.stringToMac
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import kotlin.test.assertTrue

class AbstractLinuxTest {

	val session: ClientSession = mock()
	val future: OpenFuture = mock()
	val exec: ChannelExec = mock()
	val sftp: SftpClient = mock()

	@Test
	fun getTotalMemory() {
		val linux = Fedora()  // for example (do this better with mockito)
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(exec.invertedOut).thenReturn(
				ByteArrayInputStream("MemTotal:       16345292 kB".toByteArray(Charset.forName("US-ASCII")))
		)

		val total = linux.getTotalMemory(session)
		assertEquals("16345292 kB".toSize(), total)
	}

	@Test
	fun detectPowerManagement() {
		val linux = Fedora()
		whenever(session.createExecChannel(any())).thenReturn(exec)
		whenever(exec.open()).thenReturn(future)
		whenever(exec.invertedErr).thenReturn(NullInputStream(0))
		whenever(session.createSftpClient()).thenReturn(sftp)
		val eth0: SftpClient.DirEntry = mock()
		whenever(eth0.filename).thenReturn("eth0")
		//the strange device that does not have a inet mac address - but must be tolerated
		val blah0: SftpClient.DirEntry = mock()
		whenever(blah0.filename).thenReturn("blah0")
		whenever(sftp.readDir(eq("/sys/class/net/"))).thenReturn(mutableListOf(eth0, blah0))
		whenever(sftp.read(eq("/sys/class/net/eth0/address")))
				.thenReturn(ByteArrayInputStream("12:34:56:12:34:56".toByteArray(Charsets.US_ASCII)))

		whenever(sftp.read(eq("/sys/class/net/blah0/address")))
				.thenReturn(ByteArrayInputStream("00:00:00:00".toByteArray(Charsets.US_ASCII)))

		val pms = linux.detectPowerManagement(session)

		assertEquals(1, pms.size)
		assertTrue { pms[0] is WakeOnLanInfo }
	}
}