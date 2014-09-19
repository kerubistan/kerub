package com.github.K0zka.kerub.services.socket

import org.junit.Test
import org.junit.Assert
import com.github.K0zka.kerub.services.socket.messages.PingMessage
import com.github.K0zka.kerub.services.socket.messages.SubscribeMessage
import com.github.K0zka.kerub.services.socket.messages.PongMessage
import com.github.K0zka.kerub.services.socket.messages.UnsubscribeMessage

public class JacksonBaseTest {
	Test
	fun serializeDeserialize() {
		seralizeDeserialize(PingMessage())
		seralizeDeserialize(PongMessage())
		seralizeDeserialize(SubscribeMessage(channel = "TEST"))
		seralizeDeserialize(UnsubscribeMessage(channel = "TEST"))
	}

	fun seralizeDeserialize(obj : Any) {
		val encoded = JacksonEncoder().encode(obj)
		val decoded = JacksonDecoder().decode(encoded);

		Assert.assertEquals(obj.javaClass, decoded.javaClass);

	}

}