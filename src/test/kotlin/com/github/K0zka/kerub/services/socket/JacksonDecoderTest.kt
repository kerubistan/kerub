package com.github.K0zka.kerub.services.socket

import org.junit.Test

public class JacksonDecoderTest {

	Test
	fun decode() {
		JacksonDecoder().decode("{\"@type\":\"subscribe\",\"channel\":\"test\"}")
	}
}