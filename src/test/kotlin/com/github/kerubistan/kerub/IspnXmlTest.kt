package com.github.kerubistan.kerub

import com.github.kerubistan.kerub.utils.resource
import com.github.kerubistan.kerub.utils.substringBetween
import org.junit.Test
import kotlin.test.assertFalse

class IspnXmlTest {
	@Test
	fun integrity() {
		resource("infinispan.xml").reader(Charsets.UTF_8).useLines {
			lines ->
			val paths = lines.filter { it.contains("file-store path=") }.map { it.substringBetween("\"", "\"") }

			var pathSet = emptySet<String>()
			paths.forEach {
				path ->
				assertFalse(pathSet.contains(path), "$path duplicated")
				pathSet = pathSet + path
			}
		}
	}
}