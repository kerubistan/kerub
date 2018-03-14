package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertFalse

class UnshareNfsExecutorTest {

	@Test
	fun execute() {

		val session = mock<ClientSession>()

		val executor = mock<HostCommandExecutor>()
		val hostConfigDao = mock<HostConfigurationDao>()
		val sftpClient = mock<SftpClient>()
		val execChannel = mock<ChannelExec>()
		val future = mock<OpenFuture>()


		whenever(executor.execute(eq(testHost), any<(ClientSession) -> Unit>())).then {
			(it.arguments[1] as (ClientSession) -> Unit).invoke(session)
		}

		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(eq("/etc/exports"))).then { """
			# example from the man page
			/               master(rw) trusty(rw,no_root_squash)
			/projects       proj*.local.domain(rw)
			/usr            *.local.domain(ro) @trusted(rw)
			/home/joe       pc001(rw,all_squash,anonuid=150,anongid=100)
			/pub            *(ro,insecure,all_squash)
			/srv/www        -sync,rw server @trusted @external(ro)
			/foo            2001:db8:9:e54::/64(rw) 192.0.2.0/24(rw)
			/build          buildhost[0-9].local.domain(rw)
		""".trimIndent().byteInputStream() }

		val exportsOut = ByteArrayOutputStream()
		whenever(sftpClient.write(eq("/etc/exports"))).then { exportsOut }
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(future)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { NullInputStream(0) }

		UnshareNfsExecutor(hostConfigDao, executor).execute(UnshareNfs("/foo", testHost))

		verify(session).createExecChannel(eq("exportfs -r"))
		assertFalse { exportsOut.toByteArray().toString().contains("/foo") }
	}
}