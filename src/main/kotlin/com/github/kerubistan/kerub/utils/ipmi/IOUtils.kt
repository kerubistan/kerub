package com.github.kerubistan.kerub.utils.ipmi

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred

fun ChannelFuture.then(timeout: Int, action: (Channel) -> Unit): Promise<Channel, Exception> =
		deferred<Channel, Exception>().let {
			deferred ->
			this.addListener {
				val channel = (it as ChannelFuture).channel()
				action(channel)
				deferred.resolve(channel)
			}
			deferred.promise
		}

