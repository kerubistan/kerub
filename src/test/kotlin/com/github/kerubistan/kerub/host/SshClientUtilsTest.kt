package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.getTestKey
import com.github.kerubistan.kerub.services.getFreePort
import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.UserAuth
import org.apache.sshd.server.auth.UserAuthNoneFactory
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException
import java.net.SocketAddress
import java.nio.file.FileSystems
import java.nio.file.Files
import java.security.PublicKey
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail
import org.apache.sshd.client.auth.UserAuth as ClientUserAuth
import org.apache.sshd.client.auth.password.UserAuthPasswordFactory as ClientUserAuthPasswordFactory

class SshClientUtilsTest {

	private var server: SshServer? = null
	private var client: SshClient? = null
	private var session: ClientSession? = null

	private val testUserName = System.getProperty("user.name")
	private val testUserPassword = "R3411y533cr3tP455"

	private var rootDir: File? = null

	@Before
	fun setup() {
		//allocate a free port for the ssh server
		val sshPort = getFreePort()

		//build up a test filesystem
		val tempDir = Files.createTempDirectory("tmp").toFile()
		rootDir = File(tempDir, UUID.randomUUID().toString())
		rootDir?.mkdirs()

		//start ssh server
		server = SshServer.setUpDefaultServer()
		server?.subsystemFactories = listOf(SftpSubsystemFactory())
		val virtualFileSystemFactory = VirtualFileSystemFactory()
		virtualFileSystemFactory.setUserHomeDir(testUserName, FileSystems.getDefault().getPath(rootDir!!.absolutePath))
		server?.fileSystemFactory = virtualFileSystemFactory
		server?.setPasswordAuthenticator {userName: String, password: String, serverSession: ServerSession ->
			userName == testUserName && password == testUserPassword
		}
		server?.userAuthFactories = listOf<NamedFactory<UserAuth>>(UserAuthPasswordFactory(), UserAuthNoneFactory())
		server?.keyPairProvider = SingleKeyPairProvider(getTestKey())
		server?.port = sshPort
		server?.start()

		//start ssh client
		client = requireNotNull(SshClient.setUpDefaultClient())
		client?.setServerKeyVerifier {clientSession: ClientSession, socketAddress: SocketAddress, publicKey: PublicKey -> true }
		client?.userAuthFactories = listOf<NamedFactory<ClientUserAuth>>(ClientUserAuthPasswordFactory())
		client?.start()
		val future = client?.connect(testUserName, "localhost", sshPort)?.addListener { it.session.addPasswordIdentity(testUserPassword) }
		future?.await()
		session = future?.session
		session?.auth()
	}

	@After
	fun cleanup() {
		session?.close(true)
		client?.close(true)
		server?.stop()
		rootDir?.delete()
	}

	@Test
	fun readFile() {
		session?.createSftpClient()?.use {
			it.write("test.txt").writer(charset("ASCII")).use {
				it.write("PASS")
			}
		}
		assertEquals("PASS", session?.getFileContents("test.txt"))
		//do it again to check if resource usage is fine
		assertEquals("PASS", session?.getFileContents("test.txt"))
	}

	@Test
	fun checkFileExists() {
		session?.createSftpClient()?.use {
			it.write("shouldexist").writer(charset("ASCII")).use {
				it.write("yes")
			}
		}

		assertTrue(session!!.checkFileExists("shouldexist"))
		assertFalse(session!!.checkFileExists("should-not-exist"))
	}

	@Test
	fun appendToFile() {
		session?.createSftpClient()?.use {
			it.write("test.txt").writer(charset("ASCII")).use {
				it.write("PA")
			}
		}
		session?.appendToFile("test.txt", "SS")
		assertEquals("PASS", session?.getFileContents("test.txt"))
	}

	@Test
	fun appendToNotExistingFile() {
		session?.appendToFile("test-new.txt", "PASS")
		assertEquals("PASS", session?.getFileContents("test-new.txt"))
	}

	@Test
	fun executeOrDieWithNullErrStream() {
		// GIVEN
		val clientSession = mock<ClientSession>()
		val execChannel = mock<ChannelExec>()
		whenever(clientSession.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.invertedErr).thenReturn(null)
		whenever(execChannel.invertedOut).thenReturn("all ok".toInputStream())
		whenever(execChannel.open()).thenReturn(mock())

		// WHEN
		val result = clientSession.executeOrDie("TEST or DIE")

		// THEN
		assertEquals("all ok", result)
	}

	@Test
	fun executeOrDieWithErrorOutput() {
		// GIVEN
		val clientSession = mock<ClientSession>()
		val execChannel = mock<ChannelExec>()
		whenever(clientSession.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.invertedErr).thenReturn("test error output".toInputStream())
		whenever(execChannel.invertedOut).thenReturn("all ok".toInputStream())
		whenever(execChannel.open()).thenReturn(mock())

		try {
			// WHEN
			val result = clientSession.executeOrDie("TEST or DIE")
			fail("should have thrown exception")
		} catch (exc : IOException) {
			// expected
			assertEquals("test error output", exc.message)
		}

	}

}
