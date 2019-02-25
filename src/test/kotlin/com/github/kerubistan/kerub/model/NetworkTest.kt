package com.github.kerubistan.kerub.model

import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

class NetworkTest {
	@Test
	fun validate() {
		assertThrows<IllegalStateException> {
			Network(
					id = randomUUID(),
					name = ""
			)
		}
	}
}