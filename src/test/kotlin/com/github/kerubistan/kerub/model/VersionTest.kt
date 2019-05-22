package com.github.kerubistan.kerub.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class VersionTest {

	private fun params(): List<Pair<Version, String>> {
		return listOf(
				Pair(Version("1", "2", "3"), "1.2.3"),
				Pair(Version("0", "8", "13"), "0.8-13.fc20.x86_64"),
				Pair(Version("1", "0", "19"), "1.0.19-1.fc20.x86_64"),
				Pair(Version("1", "2", null), "1.2"),
				Pair(Version("1", null, null), "1")
		             )
	}

	@Test
	fun compare() {
		for (pair in params()) {
			val parsed = Version.fromVersionString(pair.second)
			assertEquals(pair.first.major, parsed.major)
			assertEquals(pair.first.minor, parsed.minor)
			assertEquals(pair.first.build, parsed.build)
		}
	}

	@Test
	fun testToString() {
		for(pair in params()) {
			assertNotNull(pair.first.toString())
		}
		assertEquals("1.0", Version("1","0",null).toString())
		assertEquals("1", Version("1",null,null).toString())
		assertEquals("1.0.0", Version("1","0","0").toString())
	}
}