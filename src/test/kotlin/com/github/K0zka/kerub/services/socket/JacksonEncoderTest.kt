package com.github.K0zka.kerub.services.socket

import org.junit.Test
import com.github.K0zka.kerub.services.socket.messages.SubscribeMessage
import org.junit.Assert

public class JacksonEncoderTest {
	Test
	fun encode() {
		Assert.assertNotNull(JacksonEncoder().encode( SubscribeMessage("TEST") ))
	}
}

