package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import com.github.K0zka.kerub.utils.toSize
import com.github.K0zka.kerub.verify
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.ClientSession
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.OutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class LvmLvTest {

	@Mock
	var session: ClientSession? = null

	@Mock
	var execChannel: ChannelExec? = null

	@Mock
	var createExecChannel: ChannelExec? = null

	@Mock
	var openFuture: OpenFuture? = null

	val testListOutput =
			"""  Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK:root:/dev/fedora/root:9135194112B:::linear:
  eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS:testlv1:/dev/test/testlv1:1073741824B:::linear:
  U3xf04-DJFM-nXD5-682c-9w32-rcjS-pne6t2:testlv2:/dev/test/testlv2:1073741824B:::linear:"""


	@Test
	fun list() {

		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmLv.list(session!!)

		Assert.assertEquals(3, list.size)
		Assert.assertEquals("Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK", list[0].id)
		Assert.assertEquals("root", list[0].name)
		Assert.assertEquals("/dev/fedora/root", list[0].path)
		Assert.assertEquals(BigInteger("9135194112"), list[0].size)
	}

	@Test
	fun listWithVgAndLv() {

		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmLv.list(session!!, volGroupName = "testvg", volName = "testlv")

		verify(session!!).createExecChannel("lvs -o $fields $listOptions testvg/testlv")
	}

	@Test
	fun listWithVg() {

		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmLv.list(session!!, volGroupName = "testvg")

		verify(session!!).createExecChannel("lvs -o $fields $listOptions testvg")
	}

	@Test
	fun delete() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvremove"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		LvmLv.delete(session!!, "test")
	}

	@Test(expected = IOException::class)
	fun deleteAndFail() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvremove"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(ByteArrayInputStream(""" Volume group "test" not found
  Cannot process volume group test
""".toByteArray(charset("ASCII"))))

		LvmLv.delete(session!!, "test")

	}

	@Test
	fun create() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvcreate"))).thenReturn(createExecChannel)
		Mockito.`when`(createExecChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(createExecChannel?.invertedOut)
				.thenReturn(ByteArrayInputStream("  Logical volume \"test\" created.\n".toByteArray(charset("ASCII"))))
		Mockito.`when`(createExecChannel?.invertedErr).thenReturn(NullInputStream(0))

		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val volume = LvmLv.create(session!!, "test", "testlv2", "16 GB".toSize())
		Assert.assertEquals("testlv2", volume.name)
	}

	@Test
	fun createWithWarning() {
		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvcreate"))).thenReturn(createExecChannel)
		Mockito.`when`(createExecChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(createExecChannel?.invertedOut)
				.thenReturn(ByteArrayInputStream("  Logical volume \"test\" created.\n".toByteArray(charset("ASCII"))))
		Mockito.`when`(createExecChannel?.invertedErr).thenReturn(
				ByteArrayInputStream(
						("  WARNING: Sum of all thin volume sizes (20.00 PiB) exceeds the size of thin pools and " +
								"the size of whole volume group (526.81 GiB)!")
								.toByteArray(Charset.forName("ASCII"))
				)
		)

		Mockito.`when`(session?.createExecChannel(Matchers.startsWith("lvs"))).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel?.invertedOut).thenReturn(ByteArrayInputStream(testListOutput.toByteArray(charset("ASCII"))))
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		val volume = LvmLv.create(session!!, "test", "testlv2", "16 GB".toSize())
		Assert.assertEquals("testlv2", volume.name)
	}

	val monitorOutput =
			"""  ZiVgic-SEyk-g4Ji-T9fV-fPJO-LAeA-E0yZz8:home:/dev/fedora_localshot/home:161069662208B:::linear:
  0ICQXN-L7Vf-kFSO-n0UG-dV16-RvFb-DQ1tde:root:/dev/fedora_localshot/root:53687091200B:::linear:
  Dhbb1k-32MW-1Sui-Jiaz-4Bgg-euQ4-b2lF7c:swap:/dev/fedora_localshot/swap:4290772992B:::linear:
  3KdPRQ-syT1-FGdd-hWk5-DcHV-Vc5f-cH7CEl:var:/dev/fedora_localshot/var:214748364800B:::linear:
  qCnzcG-whyN-9eUF-CW5M-4Cqu-M1Kf-16eFui:full:/dev/testarea/full:1073741824B:::linear:
  aw2aFK-KdGF-06mj-W6wj-pmLH-JWTO-dqE0iI:full2:/dev/testarea/full2:1073741824B:::linear:
  HdL4jg-i7Gu-twJ3-rg6I-uFzO-9NGG-em2RXe:full3:/dev/testarea/full3:10737418240B:::linear:
  440OEU-CyMy-KZHL-r4o9-13xY-lL78-R31Puz:lvol0:/dev/testarea/lvol0:10737418240B:::linear:
  BD5Dr0-JqCr-fN8l-fWcm-pXSt-CPd8-ZqBQrG:lvol2::1073741824B:::thin,pool:0.00
  XkMLO9-aSM5-dR6t-W760-D6cg-ucqC-bHbhpi:lvol3::1073741824B:::thin,pool:0.00
  R5AD2Q-WbgX-7eXa-0Igf-r73C-nIIV-ShAYdF:lvol4::1073741824B:::thin,pool:0.00
  euH6zz-1u82-zogo-tdV1-8MQ9-uSqz-sx8Idv:lvol5::1073741824B:::thin,pool:0.00
  W2ELLW-MwtG-0NMa-f1RL-P4Qb-ze9g-0HGhOg:lvol6::1073741824B:::thin,pool:0.00
  MUwsIR-aYQV-tQVU-8K9r-mUI4-CX6d-9OE10H:thn1:/dev/testarea/thn1:6755399441055744B:::thin,sparse:0.00
  U4arGv-h6Qu-8hrm-nAtd-b8w1-ICeI-w7aMuK:thn6:/dev/testarea/thn6:15762598695796736B:::thin,sparse:0.00
--end
  ZiVgic-SEyk-g4Ji-T9fV-fPJO-LAeA-E0yZz8:home:/dev/fedora_localshot/home:161069662208B:::linear:
  0ICQXN-L7Vf-kFSO-n0UG-dV16-RvFb-DQ1tde:root:/dev/fedora_localshot/root:53687091200B:::linear:
  Dhbb1k-32MW-1Sui-Jiaz-4Bgg-euQ4-b2lF7c:swap:/dev/fedora_localshot/swap:4290772992B:::linear:
  3KdPRQ-syT1-FGdd-hWk5-DcHV-Vc5f-cH7CEl:var:/dev/fedora_localshot/var:214748364800B:::linear:
  qCnzcG-whyN-9eUF-CW5M-4Cqu-M1Kf-16eFui:full:/dev/testarea/full:1073741824B:::linear:
  aw2aFK-KdGF-06mj-W6wj-pmLH-JWTO-dqE0iI:full2:/dev/testarea/full2:1073741824B:::linear:
  HdL4jg-i7Gu-twJ3-rg6I-uFzO-9NGG-em2RXe:full3:/dev/testarea/full3:10737418240B:::linear:
  440OEU-CyMy-KZHL-r4o9-13xY-lL78-R31Puz:lvol0:/dev/testarea/lvol0:10737418240B:::linear:
  BD5Dr0-JqCr-fN8l-fWcm-pXSt-CPd8-ZqBQrG:lvol2::1073741824B:::thin,pool:0.00
  XkMLO9-aSM5-dR6t-W760-D6cg-ucqC-bHbhpi:lvol3::1073741824B:::thin,pool:0.00
  R5AD2Q-WbgX-7eXa-0Igf-r73C-nIIV-ShAYdF:lvol4::1073741824B:::thin,pool:0.00
  euH6zz-1u82-zogo-tdV1-8MQ9-uSqz-sx8Idv:lvol5::1073741824B:::thin,pool:0.00
  W2ELLW-MwtG-0NMa-f1RL-P4Qb-ze9g-0HGhOg:lvol6::1073741824B:::thin,pool:0.00
  MUwsIR-aYQV-tQVU-8K9r-mUI4-CX6d-9OE10H:thn1:/dev/testarea/thn1:6755399441055744B:::thin,sparse:0.00
  U4arGv-h6Qu-8hrm-nAtd-b8w1-ICeI-w7aMuK:thn6:/dev/testarea/thn6:15762598695796736B:::thin,sparse:0.00
--end
"""

	@Test
	fun monitor() {
		Mockito.`when`(session?.createExecChannel(Matchers.anyString())).thenReturn(execChannel)
		Mockito.`when`(execChannel?.open()).thenReturn(openFuture)
		Mockito.doAnswer {
			val out = it.arguments[0] as OutputStream
			monitorOutput.forEach {
				out.write(it.toInt())
			}
			null
		}.`when`(execChannel)!!.out = Matchers.any(OutputStream::class.java)
		Mockito.`when`(execChannel?.invertedErr).thenReturn(NullInputStream(0))

		var results = mutableListOf<List<LogicalVolume>>()

		LvmLv.monitor(session!!, {
			volumes ->
			results.add(volumes)
		})

		assertEquals(2, results.size)
	}

	@Test
	fun roundUp() {
		assertEquals("512B".toSize(), LvmLv.roundUp("0B".toSize()))
		assertEquals("512B".toSize(), LvmLv.roundUp("128B".toSize()))
		assertEquals("1KB".toSize(), LvmLv.roundUp("513B".toSize()))
		assertEquals("1KB".toSize(), LvmLv.roundUp("1KB".toSize()))
		assertEquals("2KB".toSize(), LvmLv.roundUp("2000B".toSize()))
	}

}