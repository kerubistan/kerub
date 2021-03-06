package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.KB
import io.github.kerubistan.kroki.size.MB
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.startsWith
import org.mockito.Mockito
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.OutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LvmLvTest {

	val session: ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val createExecChannel: ChannelExec = mock()
	var openFuture: OpenFuture = mock()

	private val testListOutput =
			"""  fedora:Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK:root:/dev/fedora/root:9135194112B:::linear:
  test:eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS:testlv1:/dev/test/testlv1:1073741824B:::linear:
  test:U3xf04-DJFM-nXD5-682c-9w32-rcjS-pne6t2:testlv2:/dev/test/testlv2:1073741824B:::linear:"""


	@Test
	fun list() {

		whenever(session.createExecChannel(startsWith("lvm lvs"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmLv.list(session)

		assertEquals(3, list.size)
		assertEquals("Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK", list[0].id)
		assertEquals("root", list[0].name)
		assertEquals("/dev/fedora/root", list[0].path)
		assertEquals(BigInteger("9135194112"), list[0].size)
	}

	@Test
	fun listWithVgAndLv() {

		whenever(session.createExecChannel(startsWith("lvm lvs"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmLv.list(session, volGroupName = "testvg", volName = "testlv")

		assertEquals(3, list.size)
		verify(session).createExecChannel("lvm lvs -o $fields $listOptions testvg/testlv")
	}

	@Test
	fun exists() {
		whenever(session.createExecChannel(startsWith("lvm lvs"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		assertTrue {
			LvmLv.exists(session, "testvg", "testlv1")
		}
	}

	@Test
	fun existsNotexisting() {
		whenever(session.createExecChannel(startsWith("lvm lvs"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		assertFalse {
			LvmLv.exists(session, "testvg", "NOTEXISTING")
		}
	}

	@Test
	fun listWithVg() {

		whenever(session.createExecChannel(startsWith("lvm lvs"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmLv.list(session, volGroupName = "testvg")

		assertEquals(3, list.size)
		verify(session).createExecChannel("lvm lvs -o $fields $listOptions testvg")
	}

	@Test
	fun delete() {
		whenever(session.createExecChannel(startsWith("lvm lvremove"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		LvmLv.delete(session, "test")
	}

	@Test(expected = IOException::class)
	fun deleteAndFail() {
		whenever(session.createExecChannel(startsWith("lvm lvremove"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedErr).thenReturn(ByteArrayInputStream(""" Volume group "test" not found
  Cannot process volume group test
""".toByteArray(charset("ASCII"))))

		LvmLv.delete(session, "test")

	}

	@Test
	fun create() {
		whenever(session.createExecChannel(startsWith("lvm lvcreate"))).thenReturn(createExecChannel)
		whenever(createExecChannel.open()).thenReturn(openFuture)
		whenever(createExecChannel.invertedOut)
				.thenReturn(ByteArrayInputStream("  Logical volume \"test\" created.\n".toByteArray(charset("ASCII"))))
		whenever(createExecChannel.invertedErr).thenReturn(NullInputStream(0))

		whenever(session.createExecChannel(startsWith("lvm lvs"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val volume = LvmLv.create(session, "test", "testlv2", 16.GB)
		assertEquals("testlv2", volume.name)
	}

	@Test
	fun createWithWarning() {
		whenever(session.createExecChannel(startsWith("lvm lvcreate"))).thenReturn(createExecChannel)
		whenever(createExecChannel.open()).thenReturn(openFuture)
		whenever(createExecChannel.invertedOut)
				.thenReturn(ByteArrayInputStream("  Logical volume \"test\" created.\n".toByteArray(charset("ASCII"))))
		whenever(createExecChannel.invertedErr).thenReturn(
				ByteArrayInputStream(
						("  WARNING: Sum of all thin volume sizes (20.00 PiB) exceeds the size of thin pools and " +
								"the size of whole volume group (526.81 GiB)!")
								.toByteArray(Charset.forName("ASCII"))
				)
		)

		whenever(session.createExecChannel(startsWith("lvm lvs"))).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val volume = LvmLv.create(session, "test", "testlv2", 16.GB)
		assertEquals("testlv2", volume.name)
	}

	private val monitorOutput =
			"""  fedora_localshot:ZiVgic-SEyk-g4Ji-T9fV-fPJO-LAeA-E0yZz8:home:/dev/fedora_localshot/home:161069662208B:::linear:
  fedora_localshot:0ICQXN-L7Vf-kFSO-n0UG-dV16-RvFb-DQ1tde:root:/dev/fedora_localshot/root:53687091200B:::linear:
  fedora_localshot:Dhbb1k-32MW-1Sui-Jiaz-4Bgg-euQ4-b2lF7c:swap:/dev/fedora_localshot/swap:4290772992B:::linear:
  fedora_localshot:3KdPRQ-syT1-FGdd-hWk5-DcHV-Vc5f-cH7CEl:var:/dev/fedora_localshot/var:214748364800B:::linear:
  testarea:qCnzcG-whyN-9eUF-CW5M-4Cqu-M1Kf-16eFui:full:/dev/testarea/full:1073741824B:::linear:
  testarea:aw2aFK-KdGF-06mj-W6wj-pmLH-JWTO-dqE0iI:full2:/dev/testarea/full2:1073741824B:::linear:
  testarea:HdL4jg-i7Gu-twJ3-rg6I-uFzO-9NGG-em2RXe:full3:/dev/testarea/full3:10737418240B:::linear:
  testarea:440OEU-CyMy-KZHL-r4o9-13xY-lL78-R31Puz:lvol0:/dev/testarea/lvol0:10737418240B:::linear:
  testarea:BD5Dr0-JqCr-fN8l-fWcm-pXSt-CPd8-ZqBQrG:lvol2::1073741824B:::thin,pool:0.00
  testarea:XkMLO9-aSM5-dR6t-W760-D6cg-ucqC-bHbhpi:lvol3::1073741824B:::thin,pool:0.00
  testarea:R5AD2Q-WbgX-7eXa-0Igf-r73C-nIIV-ShAYdF:lvol4::1073741824B:::thin,pool:0.00
  testarea:euH6zz-1u82-zogo-tdV1-8MQ9-uSqz-sx8Idv:lvol5::1073741824B:::thin,pool:0.00
  testarea:W2ELLW-MwtG-0NMa-f1RL-P4Qb-ze9g-0HGhOg:lvol6::1073741824B:::thin,pool:0.00
  testarea:MUwsIR-aYQV-tQVU-8K9r-mUI4-CX6d-9OE10H:thn1:/dev/testarea/thn1:6755399441055744B:::thin,sparse:0.00
  testarea:U4arGv-h6Qu-8hrm-nAtd-b8w1-ICeI-w7aMuK:thn6:/dev/testarea/thn6:15762598695796736B:::thin,sparse:0.00
--end
  fedora_localshot:ZiVgic-SEyk-g4Ji-T9fV-fPJO-LAeA-E0yZz8:home:/dev/fedora_localshot/home:161069662208B:::linear:
  fedora_localshot:0ICQXN-L7Vf-kFSO-n0UG-dV16-RvFb-DQ1tde:root:/dev/fedora_localshot/root:53687091200B:::linear:
  fedora_localshot:Dhbb1k-32MW-1Sui-Jiaz-4Bgg-euQ4-b2lF7c:swap:/dev/fedora_localshot/swap:4290772992B:::linear:
  fedora_localshot:3KdPRQ-syT1-FGdd-hWk5-DcHV-Vc5f-cH7CEl:var:/dev/fedora_localshot/var:214748364800B:::linear:
  testarea:qCnzcG-whyN-9eUF-CW5M-4Cqu-M1Kf-16eFui:full:/dev/testarea/full:1073741824B:::linear:
  testarea:aw2aFK-KdGF-06mj-W6wj-pmLH-JWTO-dqE0iI:full2:/dev/testarea/full2:1073741824B:::linear:
  testarea:HdL4jg-i7Gu-twJ3-rg6I-uFzO-9NGG-em2RXe:full3:/dev/testarea/full3:10737418240B:::linear:
  testarea:440OEU-CyMy-KZHL-r4o9-13xY-lL78-R31Puz:lvol0:/dev/testarea/lvol0:10737418240B:::linear:
  testarea:BD5Dr0-JqCr-fN8l-fWcm-pXSt-CPd8-ZqBQrG:lvol2::1073741824B:::thin,pool:0.00
  testarea:XkMLO9-aSM5-dR6t-W760-D6cg-ucqC-bHbhpi:lvol3::1073741824B:::thin,pool:0.00
  testarea:R5AD2Q-WbgX-7eXa-0Igf-r73C-nIIV-ShAYdF:lvol4::1073741824B:::thin,pool:0.00
  testarea:euH6zz-1u82-zogo-tdV1-8MQ9-uSqz-sx8Idv:lvol5::1073741824B:::thin,pool:0.00
  testarea:W2ELLW-MwtG-0NMa-f1RL-P4Qb-ze9g-0HGhOg:lvol6::1073741824B:::thin,pool:0.00
  testarea:MUwsIR-aYQV-tQVU-8K9r-mUI4-CX6d-9OE10H:thn1:/dev/testarea/thn1:6755399441055744B:::thin,sparse:0.00
  testarea:U4arGv-h6Qu-8hrm-nAtd-b8w1-ICeI-w7aMuK:thn6:/dev/testarea/thn6:15762598695796736B:::thin,sparse:0.00
--end
"""

	@Test
	fun monitor() {
		whenever(session.createExecChannel(anyString())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		Mockito.doAnswer {
			val out = it.arguments[0] as OutputStream
			monitorOutput.forEach {
				out.write(it.toInt())
			}
			null
		}.`when`(execChannel)!!.out = any()
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val results = mutableListOf<List<LogicalVolume>>()

		LvmLv.monitor(session) { volumes ->
			results.add(volumes)
		}

		assertEquals(2, results.size)
	}

	@Test
	fun roundUp() {
		assertEquals(4.MB, LvmLv.roundUp(0.toBigInteger()))
		assertEquals(4.MB, LvmLv.roundUp(128.toBigInteger()))
		assertEquals(4.MB, LvmLv.roundUp(513.toBigInteger()))
		assertEquals(4.MB, LvmLv.roundUp(1.KB))
		assertEquals(4.MB, LvmLv.roundUp(2000.toBigInteger()))
	}

	@Test
	fun remove() {
		whenever(session.createExecChannel(startsWith("lvm lvremove"))).thenReturn(createExecChannel)
		whenever(createExecChannel.open()).thenReturn(openFuture)
		whenever(createExecChannel.invertedOut).then { NullInputStream(0) }
		whenever(createExecChannel.invertedErr).then { NullInputStream(0) }

		LvmLv.remove(session, "testVg", "testLv")

		verify(session).createExecChannel(startsWith("lvm lvremove"))
	}

	@Test
	fun mirror() {
		session.mockCommandExecution("lvm lvconvert .*".toRegex())

		LvmLv.mirror(session, "test-lv", "test-vg", 1.toShort())

		session.verifyCommandExecution("lvm lvconvert .*".toRegex())
	}

	@Test
	fun mirrorWithNegative() {
		session.mockCommandExecution("lvm lvconvert .*".toRegex())

		assertThrows<IllegalArgumentException> {
			LvmLv.mirror(session, "test-lv", "test-vg", (-1).toShort())
		}

		session.verifyCommandExecution("lvm lvconvert .*".toRegex(), never())
	}

}