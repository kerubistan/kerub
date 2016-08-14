package com.github.K0zka.kerub.utils.junix.virt.vbox

import com.github.K0zka.kerub.utils.toSize
import org.junit.Test

import org.junit.Assert.*

class VBoxManageTest {

	@Test
	fun round() {
		assertEquals("512", VBoxManage.round("511.11 MB".toSize()))
		assertEquals("512", VBoxManage.round("511.5 MB".toSize()))
		assertEquals("2", VBoxManage.round("1025 KB".toSize()))
		assertEquals("1", VBoxManage.round("1024 KB".toSize()))
		assertEquals("0", VBoxManage.round("0 KB".toSize()))
		assertEquals("1", VBoxManage.round("1 KB".toSize()))
	}

}