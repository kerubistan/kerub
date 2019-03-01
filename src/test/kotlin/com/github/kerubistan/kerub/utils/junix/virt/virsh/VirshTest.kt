package com.github.kerubistan.kerub.utils.junix.virt.virsh

import com.github.kerubistan.kerub.model.display.RemoteConsoleProtocol
import com.github.kerubistan.kerub.utils.resource
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import kotlin.test.assertEquals

class VirshTest {

	val session: ClientSession = mock()

	val execChannel: ChannelExec = mock()

	val channelOpenFuture: OpenFuture = mock()

	val sftpClient: SftpClient = mock()

	@Before
	fun setup() {
		whenever(session.createExecChannel(Matchers.anyString() ?: "")).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(channelOpenFuture)
		whenever(session.createSftpClient()).thenReturn(sftpClient)
	}

	@Test
	fun create() {
		whenever(sftpClient.write(any())).thenReturn(ByteArrayOutputStream())
		whenever(execChannel.invertedErr)
				.thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut)
				.thenReturn(NullInputStream(0))
		Virsh.create(session, UUID.randomUUID(), "TEST-DOMAIN-DEF")
	}

	@Test
	fun createAndFail() {
		try {
			whenever(sftpClient.write(any())).thenReturn(ByteArrayOutputStream())
			whenever(execChannel.invertedErr)
					.thenReturn(ByteArrayInputStream("TEST ERROR".toByteArray(charset("ASCII"))))
			whenever(execChannel.invertedOut)
					.thenReturn(NullInputStream(0))
			Virsh.create(session, UUID.randomUUID(), "TEST-DOMAIN-DEF")
		} catch(e: IOException) {
			verify(sftpClient).write(any())
			verify(sftpClient).remove(any())
		}
	}

	@Test
	fun list() {
		val testOutput =
				"""8952908a-f27d-45dc-b274-0aeb7a68660a
8952908a-f27d-45dc-b274-0aeb7a68660b
8952908a-f27d-45dc-b274-0aeb7a68660c"""
		whenever(execChannel.invertedOut)
				.thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr)
				.thenReturn(NullInputStream(0))

		val ids = Virsh.list(session)

		Assert.assertEquals(ids, listOf(UUID.fromString("8952908a-f27d-45dc-b274-0aeb7a68660a"),
				UUID.fromString("8952908a-f27d-45dc-b274-0aeb7a68660b"),
				UUID.fromString("8952908a-f27d-45dc-b274-0aeb7a68660c")))
	}

	@Test
	fun suspend() {
		whenever(execChannel.invertedOut)
				.thenReturn(NullInputStream(0))
		whenever(execChannel.invertedErr)
				.thenReturn(NullInputStream(0))
		Virsh.suspend(session, UUID.randomUUID())

		Mockito.verify(session)!!.createExecChannel(Matchers.startsWith("virsh suspend") ?: "")
	}

	@Test
	fun resume() {
		whenever(execChannel.invertedOut)
				.thenReturn(NullInputStream(0))
		whenever(execChannel.invertedErr)
				.thenReturn(NullInputStream(0))
		Virsh.resume(session, UUID.randomUUID())

		Mockito.verify(session)!!.createExecChannel(Matchers.startsWith("virsh resume") ?: "")
	}

	val domStatOutput = """
Domain: 'kerub.hosts.opensuse13'
  state.state=1
  state.reason=1
  cpu.time=18957473241
  cpu.user=930000000
  cpu.system=3840000000
  balloon.current=1048576
  balloon.maximum=1048576
  vcpu.current=2
  vcpu.maximum=2
  vcpu.0.state=1
  vcpu.0.time=13470000000
  vcpu.1.state=1
  vcpu.1.time=2940000000
  net.count=1
  net.0.name=vnet0
  net.0.rx.bytes=90554
  net.0.rx.pkts=1587
  net.0.rx.errs=0
  net.0.rx.drop=0
  net.0.tx.bytes=21649
  net.0.tx.pkts=251
  net.0.tx.errs=0
  net.0.tx.drop=0
  block.count=1
  block.0.name=sda
  block.0.path=/var/lib/libvirt/images/kerub.hosts.opensuse13.qcow2
  block.0.rd.reqs=3028
  block.0.rd.bytes=97718784
  block.0.rd.times=1461331391
  block.0.wr.reqs=358
  block.0.wr.bytes=20819968
  block.0.wr.times=39983903
  block.0.fl.reqs=26
  block.0.fl.times=1958358640
  block.0.allocation=2713136640
  block.0.capacity=8589934592
  block.0.physical=2403340288

Domain: 'kerub.hosts.fedora20'
  state.state=5
  state.reason=2
  balloon.maximum=1048576
  vcpu.current=1
  vcpu.maximum=1
  block.count=1
  block.0.name=vda
  block.0.path=/var/lib/libvirt/images/kerub.hosts.fedora20.qcow2
  block.0.allocation=2076024832
  block.0.capacity=12884901888
  block.0.physical=12887130112



"""

	val domStatMultiOutput = """
Domain: 'kerub.hosts.opensuse13'
  state.state=1
  state.reason=1
  cpu.time=18957473241
  cpu.user=930000000
  cpu.system=3840000000
  balloon.current=1048576
  balloon.maximum=1048576
  vcpu.current=2
  vcpu.maximum=2
  vcpu.0.state=1
  vcpu.0.time=13470000000
  vcpu.1.state=1
  vcpu.1.time=2940000000
  net.count=1
  net.0.name=vnet0
  net.0.rx.bytes=90554
  net.0.rx.pkts=1587
  net.0.rx.errs=0
  net.0.rx.drop=0
  net.0.tx.bytes=21649
  net.0.tx.pkts=251
  net.0.tx.errs=0
  net.0.tx.drop=0
  block.count=1
  block.0.name=sda
  block.0.path=/var/lib/libvirt/images/kerub.hosts.opensuse13.qcow2
  block.0.rd.reqs=3028
  block.0.rd.bytes=97718784
  block.0.rd.times=1461331391
  block.0.wr.reqs=358
  block.0.wr.bytes=20819968
  block.0.wr.times=39983903
  block.0.fl.reqs=26
  block.0.fl.times=1958358640
  block.0.allocation=2713136640
  block.0.capacity=8589934592
  block.0.physical=2403340288

Domain: 'kerub.hosts.fedora20'
  state.state=5
  state.reason=2
  balloon.maximum=1048576
  vcpu.current=1
  vcpu.maximum=1
  block.count=1
  block.0.name=vda
  block.0.path=/var/lib/libvirt/images/kerub.hosts.fedora20.qcow2
  block.0.allocation=2076024832
  block.0.capacity=12884901888
  block.0.physical=12887130112


Domain: 'kerub.hosts.opensuse13'
  state.state=1
  state.reason=1
  cpu.time=18957473241
  cpu.user=930000000
  cpu.system=3840000000
  balloon.current=1048576
  balloon.maximum=1048576
  vcpu.current=2
  vcpu.maximum=2
  vcpu.0.state=1
  vcpu.0.time=13470000000
  vcpu.1.state=1
  vcpu.1.time=2940000000
  net.count=1
  net.0.name=vnet0
  net.0.rx.bytes=90554
  net.0.rx.pkts=1587
  net.0.rx.errs=0
  net.0.rx.drop=0
  net.0.tx.bytes=21649
  net.0.tx.pkts=251
  net.0.tx.errs=0
  net.0.tx.drop=0
  block.count=1
  block.0.name=sda
  block.0.path=/var/lib/libvirt/images/kerub.hosts.opensuse13.qcow2
  block.0.rd.reqs=3028
  block.0.rd.bytes=97718784
  block.0.rd.times=1461331391
  block.0.wr.reqs=358
  block.0.wr.bytes=20819968
  block.0.wr.times=39983903
  block.0.fl.reqs=26
  block.0.fl.times=1958358640
  block.0.allocation=2713136640
  block.0.capacity=8589934592
  block.0.physical=2403340288

Domain: 'kerub.hosts.fedora20'
  state.state=5
  state.reason=2
  balloon.maximum=1048576
  vcpu.current=1
  vcpu.maximum=1
  block.count=1
  block.0.name=vda
  block.0.path=/var/lib/libvirt/images/kerub.hosts.fedora20.qcow2
  block.0.allocation=2076024832
  block.0.capacity=12884901888
  block.0.physical=12887130112


"""

	@Test
	fun domStat() {
		var output: OutputStream? = null
		whenever(execChannel.setOut(Mockito.any(OutputStream::class.java) ?: NullOutputStream())).thenAnswer {
			output = it.arguments[0] as OutputStream
			null
		}
		whenever(execChannel.open()).thenAnswer {
			requireNotNull(output).writer(charset("ASCII")).use {
				it.write(domStatMultiOutput)
			}
			channelOpenFuture
		}
		var results: List<List<DomainStat>> = listOf()
		Virsh.domStat(session) {
			results += listOf(it)
		}
		assertEquals(2, results.size)
		assertEquals("kerub.hosts.opensuse13", results[0][0].name)
		assertEquals(2, results[0][0].vcpuMax)
		assertEquals(2, results[0][0].cpuStats.size)
	}

	@Test
	fun domStatSingle() {
		whenever(execChannel.invertedErr)
				.thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut)
				.thenReturn(ByteArrayInputStream(domStatOutput.toByteArray(charset("ASCII"))))

		val stats = Virsh.domStat(session)
		assertEquals(2, stats.size)
	}

	@Test
	fun getDisplay() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream("spice://localhost:5902\n".toByteArray()))

		val display = Virsh.getDisplay(session, UUID.randomUUID())

		assertEquals(RemoteConsoleProtocol.spice, display.first)
		assertEquals(5902, display.second)
	}

	@Test
	fun capabilities() {
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).then {
			resource("com/github/kerubistan/kerub/utils/junix/virsh/capabilities-ubuntu16-i7.xml")
		}

		val capabilities = Virsh.capabilities(session)

		assertEquals(26, capabilities.guests.size)
	}
}