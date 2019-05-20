package com.github.kerubistan.kerub.services.socket

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import io.github.kerubistan.kroki.strings.toUUID
import org.junit.Assert.assertEquals
import org.junit.Test

class ChannelSubscriptionTest {

	@Test
	fun fromChannel() {
		assertEquals(ChannelSubscription(Host::class, null), ChannelSubscription.fromChannel("/host/"))
		assertEquals(ChannelSubscription(Host::class, "945cec66-600f-483b-cdaf-fd44b797b44c".toUUID()),
				ChannelSubscription.fromChannel("/host/945cec66-600f-483b-cdaf-fd44b797b44c"))
		assertEquals(ChannelSubscription(Host::class, "945cec66-600f-483b-cdaf-fd44b797b44c".toUUID()),
				ChannelSubscription.fromChannel("/host/945cec66-600f-483b-cdaf-fd44b797b44c/"))

		assertEquals(ChannelSubscription(HostDynamic::class, "945cec66-600f-483b-cdaf-fd44b797b44c".toUUID()),
				ChannelSubscription.fromChannel("/host-dyn/945cec66-600f-483b-cdaf-fd44b797b44c"))

	}

}