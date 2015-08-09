package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.getTestKey
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version

import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.SshServer
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.common.file.SshFile
import org.apache.sshd.server.Command
import org.apache.sshd.server.CommandFactory
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback
import org.apache.sshd.server.sftp.SftpSubsystem
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Matchers
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.io.OutputStream
import kotlin.platform.platformStatic
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

RunWith(Parameterized::class)
public class HostCapabilitiesDiscovererTest(
		val distroName: String,
		val cpuArchitecture: String,
		val kernelVersion : Version,
		val distroVersion: Version,
		val commands: Map<String, String>,
		val files: Map<String, String>) {

	companion object {
		platformStatic Parameters fun parameters(): List<Array<Any>> = listOf(
				arrayOf("Fedora",
				        "x86_64",
				        Version("3","16","6"),
				        Version("20",null,null),
				        mapOf(
						        Pair("uname -s", "Linux"),
						        Pair("uname -r", "3.16.6-203.fc20.x86_64"),
						        Pair("uname -p", "x86_64"),
						        Pair("cat /proc/meminfo | grep  MemTotal", "MemTotal:        7767288 kB"),
						        Pair("rpm -qa","""texlive-auto-pst-pdf-svn23723.0.6-5.fc20.noarch
									        ceph-libs-compat-0.80.5-10.fc20.x86_64
									        opensp-1.5.2-18.fc20.x86_64
									        ustr-1.0.4-15.fc20.x86_64
									        lohit-assamese-fonts-2.5.3-2.fc20.noarch
									        perl-Package-Stash-XS-0.28-3.fc20.x86_64
									        java-1.7.0-openjdk-devel-1.7.0.71-2.5.3.0.fc20.x86_64
									        libvirt-gconfig-0.1.7-2.fc20.x86_64
									        cheese-libs-3.10.2-2.fc20.x86_64
									        """.trim()),
						        Pair("lspci -mm", """00:00.0 Host bridge: Advanced Micro Devices, Inc. [AMD] Family 14h Processor Root Complex
        00:01.0 VGA compatible controller: Advanced Micro Devices, Inc. [AMD/ATI] Wrestler [Radeon HD 7340]""")
				             ),
				        mapOf(Pair("/etc/os-release", """
						NAME=Fedora
						VERSION_ID=20
		HOME_URL="https://fedoraproject.org/"
						""".trim()))
				       ),
				arrayOf("Ubuntu",
				        "x86_64",
				        Version("3","16","6"),
				        Version("14","04",null),
				        mapOf(
						        Pair("uname -s", "Linux"),
						        Pair("uname -r", "TODO"),
						        Pair("uname -p", "x86_64"),
						        Pair("cat /proc/meminfo | grep  MemTotal", "MemTotal:        7767288 kB"),
						        Pair("dpkg-query -W --showformat \"$\\{Package\\}\t$\\{Version\\}\"", """acpid	1:2.0.10-1ubuntu3
        apparmor	2.7.102-0ubuntu3.10
        apport	2.0.1-0ubuntu17.6
        aptdaemon	1.1.1-1ubuntu5
        aptitude	0.6.6-1ubuntu1.2
        apt-transport-https	1.0.1ubuntu2.1"""),
						        Pair("lspci -mm", """00:00.0 Host bridge: Advanced Micro Devices, Inc. [AMD] Family 14h Processor Root Complex
        00:01.0 VGA compatible controller: Advanced Micro Devices, Inc. [AMD/ATI] Wrestler [Radeon HD 7340]""")
				             ),
				        mapOf(Pair("/etc/os-release", """
		NAME="Ubuntu"
		VERSION_ID="14.04"
								""".trim()))),
				arrayOf("Raspbian GNU/Linux",
				        "armv6l",
				        Version("3","12","28"),
				        Version("7",null,null),
				        mapOf(
						        Pair("uname -s", "Linux"),
						        Pair("uname -r", "3.18.11+"),
						        Pair("uname -p", "unknown"),
						        Pair("uname -m", "armv6l"),
						        Pair("cat /proc/meminfo | grep  MemTotal", "MemTotal:        496632 kB"),
						        Pair("dpkg-query -W", """adduser	3.113+nmu3
alsa-base	1.0.25+3~deb7u1
alsa-utils	1.0.25-4
apt	0.9.7.9+rpi1+deb7u7
apt-utils	0.9.7.9+rpi1+deb7u7
aptitude	0.6.8.2-1
aptitude-common	0.6.8.2-1
aspell	0.60.7~20110707-1
aspell-en	7.1-0-1
base-files	7.1wheezy8+rpi1
"""),
						        Pair("lspci -mm", """""")
				             ),
				        mapOf(Pair("/etc/os-release", """
PRETTY_NAME="Raspbian GNU/Linux 7 (wheezy)"
NAME="Raspbian GNU/Linux"
VERSION_ID="7"
VERSION="7 (wheezy)"
ID=raspbian
ID_LIKE=debian
ANSI_COLOR="1;31"
HOME_URL="http://www.raspbian.org/"
SUPPORT_URL="http://www.raspbian.org/RaspbianForums"
BUG_REPORT_URL="http://www.raspbian.org/RaspbianBugs"
								""".trim())))
		                                                      )

		fun mockFile(path : String, contents: String): SshFile {
			val ret = Mockito.mock(javaClass<SshFile>())
			Mockito.`when`(ret.doesExist()).thenReturn(true)
			Mockito.`when`(ret.getAbsolutePath()).thenReturn(path)
			Mockito.`when`(ret.getName()).thenReturn(path.substringAfterLast("/", path))
			Mockito.`when`(ret.getSize()).thenReturn(contents.length().toLong())
			Mockito.`when`(ret.isReadable()).thenReturn(true)
			Mockito.`when`(ret.isFile()).thenReturn(true)
			Mockito.`when`(ret.isWritable()).thenReturn(false)
			Mockito.`when`(ret.isDirectory()).thenReturn(false)
			val binaryContents = contents.toByteArray("ASCII")
			Mockito.`when`(ret.createInputStream(Matchers.anyLong())).then {
				ByteArrayInputStream(binaryContents)
			}
			Mockito.`when`(ret.getSize()).thenReturn(binaryContents.size().toLong())
			return ret
		}
	}

	interface RunnableCommand : Command, Runnable {

	}

	var hostManager: HostManager? = null
	var hostDao: HostDao? = null
	var commandFactory: CommandFactory? = null

	var sshClient: SshClient? = null
	var session: ClientSession? = null
	var sshServer: SshServer? = null

	Before fun setup() {
		commandFactory = Mockito.mock(javaClass<CommandFactory>())

		Mockito.`when`(commandFactory!!.createCommand(Matchers.anyString())).then {
			val command = it.getArguments()[0]
			val commandMock = Mockito.mock(javaClass<RunnableCommand>())

			var output: OutputStream? = null
			Mockito.`when`(commandMock.setOutputStream(Matchers.any(javaClass<OutputStream>())))
					.then {
						output = it.getArguments()[0] as OutputStream
						null
					}
			var callback: ExitCallback? = null;
			Mockito.doAnswer {
				callback = it.getArguments()[0] as ExitCallback
				null
			}.`when`(commandMock).setExitCallback(Matchers.any(javaClass<ExitCallback>()))
			Mockito.doAnswer {
				val writer = output?.writer("ASCII")
				writer?.appendln(commands[command])
				writer?.flush()
				callback?.onExit(0)
			}.`when`(commandMock).start(Matchers.any(javaClass<Environment>()))
			commandMock
		}

		sshServer = SshServer.setUpDefaultServer()
		sshServer!!.setPort(2222)
		sshServer!!.setPublickeyAuthenticator {s, publicKey, serverSession -> true }
		sshServer!!.setKeyPairProvider(SingleKeyPairProvider(getTestKey()))
		sshServer!!.setSubsystemFactories(listOf<NamedFactory<Command>>(SftpSubsystem.Factory()))
		sshServer!!.setFileSystemFactory {
			HostFileSystem( files.mapValues { mockFile(it.component1(), it.component2()) } )
		}
		sshServer!!.setCommandFactory(commandFactory)
		sshServer!!.start()

		sshClient = SshClient.setUpDefaultClient()
		sshClient!!.start()
		session = sshClient!!.connect("root", "127.0.0.1", 2222).await().getSession()
		session!!.addPublicKeyIdentity(getTestKey())
		session!!.auth().await()
	}

	After fun cleanup() {
		sshClient?.stop()
		sshServer?.stop()
	}

	Test fun discoverHost() {
		val capabilities = HostCapabilitiesDiscovererImpl().discoverHost(session!!)
		assertNotNull(capabilities)
		assertEquals(distroName, capabilities.distribution?.name)
		assertEquals(cpuArchitecture, capabilities.cpuArchitecture)
		assertEquals(distroVersion, capabilities.distribution?.version)
	}

	Test
	fun isDmiDecodeInstalled() {
		Assert.assertThat(
				HostCapabilitiesDiscovererImpl().isDmiDecodeInstalled(listOf(SoftwarePackage("foo", Version("1", "0", "0")), SoftwarePackage("dmidecode",Version("2","12","4")))),
				CoreMatchers.`is`(true)
		                 )
		Assert.assertThat(
				HostCapabilitiesDiscovererImpl().isDmiDecodeInstalled(listOf()),
				CoreMatchers.`is`(false)
		                 )
	}

	Test
	fun valuesOfType() {
		assertEquals(listOf("TEST"), HostCapabilitiesDiscovererImpl().valuesOfType(listOf(1, "TEST", true, 3.14), String::class))
		assertEquals(listOf<Any>(), HostCapabilitiesDiscovererImpl().valuesOfType(listOf(1, "TEST", true, 3.14), Any::class))
	}
}
