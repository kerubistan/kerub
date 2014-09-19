package com.github.K0zka.kerub.services.socket

import org.junit.Test
import com.github.K0zka.kerub.services.socket.messages.SubscribeMessage
import org.junit.Assert
import com.github.K0zka.kerub.services.socket.messages.PongMessage

public class JacksonEncoderTest {
	Test
	fun encode() {
		Assert.assertEquals("{\"@type\":\"subscribe\",\"channel\":\"TEST\"}",
		                    JacksonEncoder().encode( SubscribeMessage("TEST") ))
		Assert.assertEquals("{\"@type\":\"pong\",\"sent\":\"0\"}",JacksonEncoder().encode( PongMessage(sent = 0) ))
	}
}

