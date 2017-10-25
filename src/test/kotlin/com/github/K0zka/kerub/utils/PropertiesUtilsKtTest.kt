package com.github.K0zka.kerub.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class PropertiesUtilsKtTest {
	@Test
	fun filterByKeys() {
		assertEquals(
				propertiesOf(
						"BBB" to "222",
						"CCC" to "333"
				),
				propertiesOf(
						"AAA" to "111",
						"BBB" to "222",
						"CCC" to "333"
				).filterByKeys { it != "AAA" }
		)
	}

}