package com.github.K0zka.kerub.host

import org.junit.Test
import org.junit.Before
import org.junit.After
import org.apache.sshd.SshServer
import org.apache.sshd.SshClient
import java.net.ServerSocket
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.ClientSession
import java.security.PublicKey
import java.net.SocketAddress
import com.github.K0zka.kerub.getTestKey
import org.apache.sshd.server.UserAuth
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.server.auth.UserAuthPassword
import java.io.File
import java.util.UUID
import org.apache.sshd.server.auth.UserAuthNone
import org.apache.sshd.server.sftp.SftpSubsystem
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

public class SshClientUtilsTest {

	var server: SshServer? = null
	var client: SshClient? = null
	var session: ClientSession? = null

	val testUserName = System.getProperty("user.name")
	val testUserPassword = "R3411y533cr3tP455"

	var rootDir: File? = null

	Before
	fun setup() {
		//allocate a free port for the ssh server
		val sshPort = getFreePort()

		//build up a test filesystem
		val tempDir = Files.createTempDirectory("tmp").toFile()
		rootDir = File(tempDir, UUID.randomUUID().toString())
		rootDir?.mkdirs()

		//start ssh server
		server = SshServer.setUpDefaultServer()
		server?.setSubsystemFactories(listOf(SftpSubsystem.Factory()))
		val virtualFileSystemFactory = VirtualFileSystemFactory()
		virtualFileSystemFactory.setUserHomeDir(testUserName, rootDir!!.getAbsolutePath())
		server?.setFileSystemFactory(virtualFileSystemFactory)
		server?.setPasswordAuthenticator {(userName: String, password: String, serverSession: ServerSession) ->
			userName == testUserName && password == testUserPassword
		}
		server?.setUserAuthFactories(listOf<NamedFactory<UserAuth>>(UserAuthPassword.Factory(), UserAuthNone.Factory()))
		server?.setKeyPairProvider(SingleKeyPairProvider(getTestKey()))
		server?.setPort(sshPort)
		server?.start()

		//start ssh client
		client = SshClient.setUpDefaultClient()
		client?.setServerKeyVerifier {(clientSession: ClientSession, socketAddress: SocketAddress, publicKey: PublicKey) -> true }
		client?.setUserAuthFactories(listOf<NamedFactory<org.apache.sshd.client.UserAuth>>(org.apache.sshd.client.auth.UserAuthPassword.Factory()))
		client?.start()
		session = client?.connect(testUserName, "localhost", sshPort)?.addListener { it.getSession().addPasswordIdentity(testUserPassword) }?.await()?.getSession()
		session?.auth()
	}

	After
	fun cleanup() {
		session?.close(true)
		client?.close(true)
		server?.stop()
		rootDir?.delete()
	}

	fun getFreePort(): Int =
			ServerSocket(0).use {
				return it.getLocalPort()
			}

	Test
	fun readFile() {
		session?.createSftpClient()?.use {
			it.write("test.txt").writer("ASCII").use {
				it.write("PASS")
			}
		}
		assertEquals("PASS", session?.getFileContents("test.txt"))
		//do it again to check if resource usage is fine
		assertEquals("PASS", session?.getFileContents("test.txt"))
	}

	Test
	fun checkFileExists() {
		session?.createSftpClient()?.use {
			it.write("shouldexist").writer("ASCII").use {
				it.write("yes")
			}
		}

		assertTrue(session!!.checkFileExists("shouldexist"))
		assertFalse(session!!.checkFileExists("should-not-exist"))
	}

	Test
	fun appendToFile() {
		session?.createSftpClient()?.use {
			it.write("test.txt").writer("ASCII").use {
				it.write("PA")
			}
		}
		session?.appendToFile("test.txt", "SS")
		assertEquals("PASS", session?.getFileContents("test.txt"))
	}

	Test
	fun appendToNotExistingFile() {
		session?.appendToFile("test-new.txt", "PASS")
		assertEquals("PASS", session?.getFileContents("test-new.txt"))
	}

}
