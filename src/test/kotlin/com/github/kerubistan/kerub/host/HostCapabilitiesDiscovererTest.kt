//package com.github.kerubistan.kerub.host
//
//import com.github.kerubistan.kerub.data.HostDao
//import com.github.kerubistan.kerub.getTestKey
//import com.github.kerubistan.kerub.model.SoftwarePackage
//import com.github.kerubistan.kerub.model.Version
//import org.apache.sshd.client.SshClient
//import org.apache.sshd.client.session.ClientSession
//import org.apache.sshd.common.NamedFactory
//import org.apache.sshd.common.file.SshFile
//import org.apache.sshd.server.Command
//import org.apache.sshd.server.CommandFactory
//import org.apache.sshd.server.Environment
//import org.apache.sshd.server.ExitCallback
//import org.apache.sshd.server.SshServer
//import org.apache.sshd.server.subsystem.sftp.SftpSubsystem
//import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory
//import org.hamcrest.CoreMatchers
//import org.junit.After
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.junit.runners.Parameterized
//import org.junit.runners.Parameterized.Parameters
//import org.mockito.Matchers
//import org.mockito.Mockito
//import java.io.ByteArrayInputStream
//import java.io.OutputStream
//import java.util.EnumSet
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull
//
//@RunWith(Parameterized::class) class HostCapabilitiesDiscovererTest(
//		val distroName: String,
//		val cpuArchitecture: String,
//		val kernelVersion: Version,
//		val distroVersion: Version,
//		val commands: Map<String, String>,
//		val files: Map<String, String>,
//		val directories: Map<String, List<String>>
//) {
//
//	companion object {
//
//		val fedora20 = arrayOf("Fedora",
//				"x86_64",
//				Version("3", "16", "6"),
//				Version("20", null, null),
//				mapOf(
//						"uname -s" to "Linux",
//						"uname -r" to "3.16.6-203.fc20.x86_64",
//						"uname -p" to "x86_64",
//						"cat /proc/meminfo | grep  MemTotal" to "MemTotal:        7767288 kB",
//						"rpm -qa" to """texlive-auto-pst-pdf-svn23723.0.6-5.fc20.noarch
//													ceph-libs-compat-0.80.5-10.fc20.x86_64
//													opensp-1.5.2-18.fc20.x86_64
//													ustr-1.0.4-15.fc20.x86_64
//													lohit-assamese-fonts-2.5.3-2.fc20.noarch
//													perl-Package-Stash-XS-0.28-3.fc20.x86_64
//													java-1.7.0-openjdk-devel-1.7.0.71-2.5.3.0.fc20.x86_64
//													libvirt-gconfig-0.1.7-2.fc20.x86_64
//													cheese-libs-3.10.2-2.fc20.x86_64
//													""".trim(),
//						"lspci -mm" to """00:00.0 Host bridge: Advanced Micro Devices, Inc. [AMD] Family 14h Processor Root Complex
//				00:01.0 VGA compatible controller: Advanced Micro Devices, Inc. [AMD/ATI] Wrestler [Radeon HD 7340]""",
//						"vgs -o vg_uuid,vg_name,vg_size,vg_free,vg_extent_count,vg_free_count --noheadings --units b --separator=," to
//								"""  gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g,fedora_dhcp130-218,999670415360B,502343401472B,238340,119768""",
//						"pvs -o pv_uuid,pv_name,pv_size,pv_free,vg_uuid --noheadings --units b --separator=," to
//								"""  KNQsfE-ddlI-PEnl-3S7i-qu3U-8w1X-l6Nen1,/dev/sda2,166673252352B,0B,gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g
//  02YYyV-qaA1-hSo7-M5Ua-5zx4-IzQm-f6eLRq,/dev/sda3,832997163008B,299804655616B,gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g"""
//
//				),
//				mapOf("/etc/os-release" to """
//								NAME=Fedora
//								VERSION_ID=20
//				HOME_URL="https://fedoraproject.org/"
//								""".trim(),
//						"/sys/class/net/eth0/address" to "b8:88:e3:98:c2:0c"),
//				mapOf(
//						"/sys/class/net/" to listOf("eth0"),
//						"/sys/class/net/eth0" to listOf("address")
//				)
//		)
//		val ubuntu14_04 = arrayOf("Ubuntu",
//				"x86_64",
//				Version("3", "16", "6"),
//				Version("14", "04", null),
//				mapOf(
//						"uname -s" to "Linux",
//						"uname -r" to "TODO",
//						"uname -p" to "x86_64",
//						"cat /proc/meminfo | grep  MemTotal" to "MemTotal:        7767288 kB",
//						"dpkg-query -W --showformat \"$\\{Package\\}\t$\\{Version\\}\"" to """acpid	1:2.0.10-1ubuntu3
//apparmor	2.7.102-0ubuntu3.10
//apport	2.0.1-0ubuntu17.6
//aptdaemon	1.1.1-1ubuntu5
//aptitude	0.6.6-1ubuntu1.2
//apt-transport-https	1.0.1ubuntu2.1""",
//						"lspci -mm" to """00:00.0 Host bridge: Advanced Micro Devices, Inc. [AMD] Family 14h Processor Root Complex
//				00:01.0 VGA compatible controller: Advanced Micro Devices, Inc. [AMD/ATI] Wrestler [Radeon HD 7340]""",
//						"vgs -o vg_uuid,vg_name,vg_size,vg_free,vg_extent_count,vg_free_count --noheadings --units b --separator=," to
//								"""  gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g,fedora_dhcp130-218,999670415360B,502343401472B,238340,119768""",
//						"pvs -o pv_uuid,pv_name,pv_size,pv_free,vg_uuid --noheadings --units b --separator=," to
//								"""  KNQsfE-ddlI-PEnl-3S7i-qu3U-8w1X-l6Nen1,/dev/sda2,166673252352B,0B,gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g
//  02YYyV-qaA1-hSo7-M5Ua-5zx4-IzQm-f6eLRq,/dev/sda3,832997163008B,299804655616B,gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g"""
//
//				),
//				mapOf("/etc/os-release" to """
//				NAME="Ubuntu"
//				VERSION_ID="14.04"
//										""".trim(),
//						"/sys/class/net/eth0/address" to "b8:88:e3:98:c2:0c"),
//				mapOf(
//						"/sys/class/net/" to listOf("eth0"),
//						"/sys/class/net/eth0" to listOf("address")
//				)
//		)
//		val raspbian7 = arrayOf("Raspbian GNU/Linux",
//				"armv6l",
//				Version("3", "12", "28"),
//				Version("7", null, null),
//				mapOf(
//						"uname -s" to "Linux",
//						"uname -r" to "3.18.11+",
//						"uname -p" to "unknown",
//						"uname -m" to "armv6l",
//						"cat /proc/meminfo | grep  MemTotal" to "MemTotal:        496632 kB",
//						"dpkg-query -W" to """adduser	3.113+nmu3
//alsa-base	1.0.25+3~deb7u1
//alsa-utils	1.0.25-4
//apt	0.9.7.9+rpi1+deb7u7
//apt-utils	0.9.7.9+rpi1+deb7u7
//aptitude	0.6.8.2-1
//aptitude-common	0.6.8.2-1
//aspell	0.60.7~20110707-1
//aspell-en	7.1-0-1
//base-files	7.1wheezy8+rpi1
//		""",
//						"lspci -mm" to """""",
//						"vgs -o vg_uuid,vg_name,vg_size,vg_free,vg_extent_count,vg_free_count --noheadings --units b --separator=," to
//								"""  gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g,fedora_dhcp130-218,999670415360B,502343401472B,238340,119768""",
//						"pvs -o pv_uuid,pv_name,pv_size,pv_free,vg_uuid --noheadings --units b --separator=," to
//								"""  KNQsfE-ddlI-PEnl-3S7i-qu3U-8w1X-l6Nen1,/dev/sda2,166673252352B,0B,gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g
//  02YYyV-qaA1-hSo7-M5Ua-5zx4-IzQm-f6eLRq,/dev/sda3,832997163008B,299804655616B,gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g"""
//				),
//				mapOf("/etc/os-release" to """
//		PRETTY_NAME="Raspbian GNU/Linux 7 (wheezy)"
//		NAME="Raspbian GNU/Linux"
//		VERSION_ID="7"
//		VERSION="7 (wheezy)"
//		ID=raspbian
//		ID_LIKE=debian
//		ANSI_COLOR="1;31"
//		HOME_URL="http://www.raspbian.org/"
//		SUPPORT_URL="http://www.raspbian.org/RaspbianForums"
//		BUG_REPORT_URL="http://www.raspbian.org/RaspbianBugs"
//										""".trim(),
//						"/sys/class/net/eth0/address" to "b8:88:e3:98:c2:0c"),
//				mapOf(
//						"/sys/class/net/" to listOf("eth0"),
//						"/sys/class/net/eth0" to listOf("address")
//				)
//		)
//
//
//		@JvmStatic @Parameters fun parameters(): List<Array<Any>> {
//			return listOf(
//					fedora20,
//					ubuntu14_04,
//					raspbian7
//			)
//		}
//
//		private fun mockDirectory(path: String, entries: List<String>): SshFile {
//			val ret = Mockito.mock(SshFile::class.java)
//			Mockito.`when`(ret.doesExist()).thenReturn(true)
//			Mockito.`when`(ret.absolutePath).thenReturn(path)
//			Mockito.`when`(ret.name).thenReturn(path.substringAfterLast("/", path))
//			Mockito.`when`(ret.size).thenReturn(0)
//			Mockito.`when`(ret.isReadable).thenReturn(true)
//			Mockito.`when`(ret.isFile).thenReturn(false)
//			Mockito.`when`(ret.isWritable).thenReturn(false)
//			Mockito.`when`(ret.isDirectory).thenReturn(true)
//			val subdirs = entries.map { mockDirectory(it, listOf()) }
//			Mockito.`when`(ret.listSshFiles()).thenReturn(subdirs)
//			Mockito.`when`(ret.getAttributes(Matchers.anyBoolean())).thenReturn(mapOf(
//					SshFile.Attribute.Owner to "owner",
//					SshFile.Attribute.Group to "group",
//					SshFile.Attribute.Size to 0.toLong(),
//					SshFile.Attribute.IsDirectory to false,
//					SshFile.Attribute.IsSymbolicLink to false,
//					SshFile.Attribute.IsRegularFile to false,
//					SshFile.Attribute.Permissions to EnumSet.noneOf(SshFile.Permission::class.java),
//					SshFile.Attribute.LastModifiedTime to 0.toLong()
//			))
//			return ret
//		}
//
//		fun mockFile(path: String, contents: String): SshFile {
//			val ret = Mockito.mock(SshFile::class.java)
//			Mockito.`when`(ret.doesExist()).thenReturn(true)
//			Mockito.`when`(ret.absolutePath).thenReturn(path)
//			Mockito.`when`(ret.name).thenReturn(path.substringAfterLast("/", path))
//			Mockito.`when`(ret.size).thenReturn(contents.length.toLong())
//			Mockito.`when`(ret.isReadable).thenReturn(true)
//			Mockito.`when`(ret.isFile).thenReturn(true)
//			Mockito.`when`(ret.isWritable).thenReturn(false)
//			Mockito.`when`(ret.isDirectory).thenReturn(false)
//			val binaryContents = contents.toByteArray(charset("ASCII"))
//			Mockito.`when`(ret.createInputStream(Matchers.anyLong())).then {
//				ByteArrayInputStream(binaryContents)
//			}
//			Mockito.`when`(ret.size).thenReturn(binaryContents.size.toLong())
//			return ret
//		}
//	}
//
//	interface RunnableCommand : Command, Runnable {
//
//	}
//
//	var hostManager: HostManager? = null
//	var hostDao: HostDao? = null
//	var commandFactory: CommandFactory? = null
//
//	var sshClient: SshClient? = null
//	var session: ClientSession? = null
//	var sshServer: SshServer? = null
//
//	@Before fun setup() {
//		commandFactory = Mockito.mock(CommandFactory::class.java)
//
//		Mockito.`when`(commandFactory!!.createCommand(Matchers.anyString())).then {
//			val command = it.arguments[0]
//			val commandMock = Mockito.mock(RunnableCommand::class.java)
//
//			var output: OutputStream? = null
//			Mockito.`when`(commandMock.setOutputStream(Matchers.any(OutputStream::class.java)))
//					.then {
//						output = it.arguments[0] as OutputStream
//						null
//					}
//			var callback: ExitCallback? = null;
//			Mockito.doAnswer {
//				callback = it.arguments[0] as ExitCallback
//				null
//			}.`when`(commandMock).setExitCallback(Matchers.any(ExitCallback::class.java))
//			Mockito.doAnswer {
//				val writer = output?.writer(charset("ASCII"))
//				writer?.appendln(commands[command])
//				writer?.flush()
//				callback?.onExit(0)
//			}.`when`(commandMock).start(Matchers.any(Environment::class.java))
//			commandMock
//		}
//
//		sshServer = SshServer.setUpDefaultServer()
//		sshServer!!.port = 2222
//		sshServer!!.setPublickeyAuthenticator { s, publicKey, serverSession -> true }
//		sshServer!!.keyPairProvider = SingleKeyPairProvider(getTestKey())
//		sshServer!!.subsystemFactories = listOf<NamedFactory<Command>>(SftpSubsystemFactory())
//		sshServer!!.setFileSystemFactory {
//
//		}
//		sshServer!!.setFileSystemFactory {
//			HostFileSystem(
//					files.mapValues { mockFile(it.key, it.value) } +
//							directories.mapValues { mockDirectory(it.key, it.value) }
//			)
//		}
//		sshServer!!.commandFactory = commandFactory
//		sshServer!!.start()
//
//		sshClient = SshClient.setUpDefaultClient()
//		sshClient!!.start()
//		val future = sshClient!!.connect("root", "127.0.0.1", 2222)
//		future.await()
//		session = future.session
//		session!!.addPublicKeyIdentity(getTestKey())
//		session!!.auth().await()
//	}
//
//	@After fun cleanup() {
//		sshClient?.stop()
//		sshServer?.stop()
//	}
//
//	@Test fun discoverHost() {
//		val capabilities = HostCapabilitiesDiscovererImpl().discoverHost(session!!)
//		assertNotNull(capabilities)
//		assertEquals(distroName, capabilities.distribution?.name)
//		assertEquals(cpuArchitecture, capabilities.cpuArchitecture)
//		assertEquals(distroVersion, capabilities.distribution?.version)
//	}
//
//	@Test
//	fun isDmiDecodeInstalled() {
//		Assert.assertThat(
//				HostCapabilitiesDiscovererImpl().isDmiDecodeInstalled(listOf(SoftwarePackage("foo", Version("1", "0", "0")), SoftwarePackage("dmidecode", Version("2", "12", "4")))),
//				CoreMatchers.`is`(true)
//		)
//		Assert.assertThat(
//				HostCapabilitiesDiscovererImpl().isDmiDecodeInstalled(listOf()),
//				CoreMatchers.`is`(false)
//		)
//	}
//
//	@Test
//	fun valuesOfType() {
//		assertEquals(listOf("TEST"), HostCapabilitiesDiscovererImpl().valuesOfType(listOf(1, "TEST", true, 3.14), String::class))
//		assertEquals(listOf<Any>(), HostCapabilitiesDiscovererImpl().valuesOfType(listOf(1, "TEST", true, 3.14), Any::class))
//	}
//}
