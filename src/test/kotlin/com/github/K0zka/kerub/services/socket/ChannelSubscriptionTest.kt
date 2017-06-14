package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.utils.toUUID
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