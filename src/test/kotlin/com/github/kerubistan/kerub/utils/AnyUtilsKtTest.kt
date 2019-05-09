package com.github.kerubistan.kerub.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class AnyUtilsKtTest {

	@Test
	fun equalsAnyOf() {
		assertTrue("A".equalsAnyOf("A", "B", "C"))
		assertTrue("B".equalsAnyOf("A", "B", "C"))
		assertFalse("D".equalsAnyOf("A", "B", "C"))
		assertTrue(1.equalsAnyOf(1, 2, 3, 4))
	}

	data class Folder(
			val name: String,
			val subFolders: List<Folder> = listOf()
	)

	@Test
	fun browse() {
		assertEquals(
				setOf("var", "etc"),
				Folder("/", listOf(Folder("var"), Folder("etc"), Folder("home")))
						.browse(selector = Folder::subFolders) { it.name.length == 3 }
						.map { it.name }.toSet()
		)
	}

}