package com.github.kerubistan.kerub.utils.ipmi

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.util.concurrent.GenericFutureListener
import org.junit.Test

class IOUtilsKtTest {
	@Test
	fun then() {
		val channelFuture = mock<ChannelFuture>()
		val future = mock<ChannelFuture>()
		val channel = mock<Channel>()
		whenever(future.channel()).thenReturn(channel)
		whenever(channelFuture.addListener(any())).then {
			(it.arguments[0] as GenericFutureListener<ChannelFuture>).operationComplete(future)
			channelFuture
		}
		val action = mock<(Channel) -> Unit>()
		channelFuture.then(1000, action)

		verify(action).invoke(eq(channel))
	}
}