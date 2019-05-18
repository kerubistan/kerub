package com.github.kerubistan.kerub.utils.junix.storagemanager.gvinum

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.io.resourceToString
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import kotlin.test.assertFalse

class GVinumTest {

	val session : ClientSession = mock()
	val ftp : SftpClient = mock()

	@Test
	fun parseDriveList() {
		val list = GVinum.parseDriveList(
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/drives.txt")
		)
		verifyDrivesList(list)
	}

	@Test
	fun parseDriveListSingle() {
		val list = GVinum.parseDriveList(
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/drives-single.txt")
		)
		assertEquals(1, list.size)
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
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/subdisks.txt")
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
		session.mockCommandExecution(
				"gvinum ld.*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/drives.txt")
		)
		val drives = GVinum.listDrives(session)

		verifyDrivesList(drives)
	}

	@Test
	fun listSubDisks() {
		session.mockCommandExecution(
				"gvinum ls.*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/subdisks.txt")
		)
		val subdisks = GVinum.listSubDisks(session)

		verifySubDisks(subdisks)
	}

	@Test
	fun listVolumes() {
		session.mockCommandExecution(
				"gvinum lv.*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/volumes.txt")
		)
		val volumes = GVinum.listVolumes(session)
		verifyVolumesList(volumes)
	}

	@Test
	fun parseVolumeList() {
		val volumes = GVinum.parseVolumeList(
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/volumes.txt")
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
		session.mockCommandExecution(
				"gvinum lp.*".toRegex(),
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/plexes.txt")
		)
		val plexes = GVinum.listPlexes(session)

		verifyPlexesList(plexes)

	}
	@Test
	fun parsePlexesList() {
		val plexes = GVinum.parsePlexesList(
				resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/plexes.txt")
		)
		verifyPlexesList(plexes)
	}

	@Test
	fun createVolume() {
		session.mockCommandExecution("gvinum create.*".toRegex())
		whenever(session.createSftpClient()).thenReturn(ftp)
		val outputStream = ByteArrayOutputStream()
		whenever(ftp.write(any())).thenReturn(outputStream)

		GVinum.createSimpleVolume(session, "test-vol", "ada1", "100 GB".toSize())

		verify(ftp).remove(any())
		assertTrue(outputStream.toByteArray().isNotEmpty())
	}

	private fun verifyPlexesList(plexes: List<GvinumPlex>) {
		assertEquals(3, plexes.size)
		assertTrue(plexes[0].up)
		assertEquals(1, plexes[0].subdisks)
		assertEquals("testvol1", plexes[0].volume)
		assertEquals("testvol1.p0", plexes[0].name)
	}

	@Test
	fun monitorDrives() {
		session.mockProcess(
				".*".toRegex(),
				output = resourceToString("com/github/kerubistan/kerub/utils/junix/storagemanager/gvinum/monitor-drives.txt"))

		var drives : List<List<GvinumDrive>> = listOf()
		GVinum.monitorDrives(session) {
			drives += listOf(it)
		}

		assertFalse(drives.isEmpty())
	}

}