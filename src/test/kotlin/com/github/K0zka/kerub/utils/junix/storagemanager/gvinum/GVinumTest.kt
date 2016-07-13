package com.github.K0zka.kerub.utils.junix.storagemanager.gvinum

import com.github.K0zka.kerub.utils.resource
import com.github.K0zka.kerub.utils.resourceToString
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigInteger

class GVinumTest {

	val session : ClientSession = mock()
	val execChannel : ChannelExec = mock()
	val future : OpenFuture = mock()

	@Test
	fun parseDriveList() {
		val list = GVinum.parseDriveList(
				resourceToString("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/drives.txt")
		)
		verifyDrivesList(list)
	}

	private fun verifyDrivesList(list: List<GvinumDrive>) {
		assertEquals(2, list.size)
		assertEquals("5368573440 B".toSize(), list[0].size)
		assertEquals("ada2", list[0].device)
		assertEquals("b", list[0].name)
		assertTrue(list[0].up)
		assertEquals(BigInteger.ZERO, list[0].used)
		assertEquals("5368573440 B".toSize(), list[0].available)
	}

	@Test
	fun parseSubDiskList() {
		val list = GVinum.parseSubDiskList(
				resourceToString("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/subdisks.txt")
		)
		verifySubDisks(list)
	}

	private fun verifySubDisks(list: List<GvinumSubDisk>) {
		assertEquals(5, list.size)
		assertEquals("testvol1.p0.s0", list[0].name)
		assertEquals("a", list[0].drive)
		assertTrue(list[0].up)
	}

	@Test
	fun listDrives() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(future)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/drives.txt")
		}
		val drives = GVinum.listDrives(session)

		verifyDrivesList(drives)

	}

	@Test
	fun listSubDisks() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(future)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/subdisks.txt")
		}
		val subdisks = GVinum.listSubDisks(session)

		verifySubDisks(subdisks)
	}

	@Test
	fun listVolumes() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(future)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/volumes.txt")
		}

		val volumes = GVinum.listVolumes(session)
		verifyVolumesList(volumes)
	}

	@Test
	fun parseVolumeList() {
		val volumes = GVinum.parseVolumeList(
				resourceToString("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/volumes.txt")
		)
		verifyVolumesList(volumes)
	}

	private fun verifyVolumesList(volumes: List<GvinumVolume>) {
		assertEquals(3, volumes.size)
		assertEquals("testvol1", volumes[0].name)
		assertEquals(BigInteger("536870912"), volumes[0].size)
		assertTrue(volumes[0].up)
	}

	@Test
	fun listPlexes() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(future)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then {
			resource("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/plexes.txt")
		}
		val plexes = GVinum.listPlexes(session)

		verifyPlexesList(plexes)

	}
	@Test
	fun parsePlexesList() {
		val plexes = GVinum.parsePlexesList(
				resourceToString("com/github/K0zka/kerub/utils/junix/storagemanager/gvinum/plexes.txt")
		)
		verifyPlexesList(plexes)
	}

	private fun verifyPlexesList(plexes: List<GvinumPlex>) {
		assertEquals(3, plexes.size)
		assertTrue(plexes[0].up)
		assertEquals(1, plexes[0].subdisks)
		assertEquals("testvol1", plexes[0].volume)
		assertEquals("testvol1.p0", plexes[0].name)
	}

}