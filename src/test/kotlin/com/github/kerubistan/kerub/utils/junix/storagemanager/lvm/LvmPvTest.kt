package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.io.ByteArrayInputStream
import java.math.BigInteger
import kotlin.test.assertEquals

class LvmPvTest {

	val session : ClientSession = mock()
	val execChannel: ChannelExec = mock()
	val createExecChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()

	companion object {
		const val testOutput = """  KNQsfE-ddlI-PEnl-3S7i-qu3U-8w1X-l6Nen1:/dev/sda2:166673252352B:0B:gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g:vg-1
  02YYyV-qaA1-hSo7-M5Ua-5zx4-IzQm-f6eLRq:/dev/sda3:832997163008B:299804655616B:gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g:vg-1
"""

	}

	@Test
	fun list() {
		whenever(session.createExecChannel(argThat { startsWith("lvm pvs") })).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream(testOutput.toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmPv.list(session)

		assertEquals(2, list.size)

		assertEquals("166673252352 B".toSize(), list[0].size)
		assertEquals(BigInteger.ZERO, list[0].freeSize)
		assertEquals("/dev/sda2", list[0].device)
		assertEquals("gEUr1s-SwpD-vwZ4-trFZ-ZxJp-7kAr-E0QA5g", list[0].volumeGroupId)
		assertEquals("vg-1", list[0].volumeGroupName)
	}

	@Test
	fun listEmpty() {
		whenever(session.createExecChannel(argThat { startsWith("lvm pvs") })).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream("\n".toByteArray(charset("ASCII"))))
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

		val list = LvmPv.list(session)

		assertEquals(listOf(), list)
	}

	@Test
	fun move() {
		session.mockCommandExecution("lvm pvmove.*".toRegex())
		LvmPv.move(session, "/dev/sdd")
		session.verifyCommandExecution("lvm pvmove.*".toRegex())
	}

	@Test
	fun monitor() {
		session.mockProcess("bash.*".toRegex(), output ="""  9pySAz-Uot3-JrcR-IJXJ-moDI-c3GF-02hPgW:/dev/sda1:322118352896B:36498833408B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  orJJdF-SU8F-iDHw-5Lu4-aLcn-hSAT-eTrB4J:/dev/sda2:678076350464B:98213822464B:vLC3jw-pbd1-cTyH-PMS1-OxCO-CvRi-VyXxYQ:testarea
  gmPpnr-DoLB-67uq-iEw3-B5lc-FL9D-6qBcl2:/dev/sdb:256058064896B:63854084096B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  7A1M6r-BiSs-AnCC-5Vje-RbY5-TW0k-WQOg8W:/dev/system/centos-7-6:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  y5cxol-Z2gH-4Bne-BaWm-jWER-Y7YE-7wIW80:/dev/system/centos-7-7:2143289344B:1065353216B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  5todQQ-nqmh-l9Qt-GxWl-d8is-wu6k-EQDWWN:/dev/system/centos-7-8:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  YziNH8-gaaG-9cXP-XESg-WleJ-BMs5-V2ks1V:/dev/system/centos-7-9:2143289344B:528482304B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
--end
  9pySAz-Uot3-JrcR-IJXJ-moDI-c3GF-02hPgW:/dev/sda1:322118352896B:36498833408B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  orJJdF-SU8F-iDHw-5Lu4-aLcn-hSAT-eTrB4J:/dev/sda2:678076350464B:98213822464B:vLC3jw-pbd1-cTyH-PMS1-OxCO-CvRi-VyXxYQ:testarea
  gmPpnr-DoLB-67uq-iEw3-B5lc-FL9D-6qBcl2:/dev/sdb:256058064896B:63854084096B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  7A1M6r-BiSs-AnCC-5Vje-RbY5-TW0k-WQOg8W:/dev/system/centos-7-6:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  y5cxol-Z2gH-4Bne-BaWm-jWER-Y7YE-7wIW80:/dev/system/centos-7-7:2143289344B:1065353216B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  5todQQ-nqmh-l9Qt-GxWl-d8is-wu6k-EQDWWN:/dev/system/centos-7-8:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  YziNH8-gaaG-9cXP-XESg-WleJ-BMs5-V2ks1V:/dev/system/centos-7-9:2143289344B:528482304B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
--end
  9pySAz-Uot3-JrcR-IJXJ-moDI-c3GF-02hPgW:/dev/sda1:322118352896B:36498833408B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  orJJdF-SU8F-iDHw-5Lu4-aLcn-hSAT-eTrB4J:/dev/sda2:678076350464B:98213822464B:vLC3jw-pbd1-cTyH-PMS1-OxCO-CvRi-VyXxYQ:testarea
  gmPpnr-DoLB-67uq-iEw3-B5lc-FL9D-6qBcl2:/dev/sdb:256058064896B:63854084096B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  7A1M6r-BiSs-AnCC-5Vje-RbY5-TW0k-WQOg8W:/dev/system/centos-7-6:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  y5cxol-Z2gH-4Bne-BaWm-jWER-Y7YE-7wIW80:/dev/system/centos-7-7:2143289344B:1065353216B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  5todQQ-nqmh-l9Qt-GxWl-d8is-wu6k-EQDWWN:/dev/system/centos-7-8:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  YziNH8-gaaG-9cXP-XESg-WleJ-BMs5-V2ks1V:/dev/system/centos-7-9:2143289344B:528482304B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
--end
  9pySAz-Uot3-JrcR-IJXJ-moDI-c3GF-02hPgW:/dev/sda1:322118352896B:36498833408B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  orJJdF-SU8F-iDHw-5Lu4-aLcn-hSAT-eTrB4J:/dev/sda2:678076350464B:98213822464B:vLC3jw-pbd1-cTyH-PMS1-OxCO-CvRi-VyXxYQ:testarea
  gmPpnr-DoLB-67uq-iEw3-B5lc-FL9D-6qBcl2:/dev/sdb:256058064896B:63854084096B:HS7Pxs-uWRe-9fj6-IMQ7-teEP-C5pZ-2M2a43:system
  7A1M6r-BiSs-AnCC-5Vje-RbY5-TW0k-WQOg8W:/dev/system/centos-7-6:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  y5cxol-Z2gH-4Bne-BaWm-jWER-Y7YE-7wIW80:/dev/system/centos-7-7:2143289344B:1065353216B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  5todQQ-nqmh-l9Qt-GxWl-d8is-wu6k-EQDWWN:/dev/system/centos-7-8:2143289344B:0B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
  YziNH8-gaaG-9cXP-XESg-WleJ-BMs5-V2ks1V:/dev/system/centos-7-9:2143289344B:528482304B:NzWoaN-pcHP-qAd5-VYYP-r0zn-yEqh-79fS2t:test
--end
""")
		val callback = mock<(List<PhysicalVolume>) -> Unit>()
		LvmPv.monitor(session, callback)

		verify(callback, times(4)).invoke(argThat { size == 7 })
	}
}