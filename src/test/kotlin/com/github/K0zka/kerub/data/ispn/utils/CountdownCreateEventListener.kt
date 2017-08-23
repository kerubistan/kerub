package com.github.K0zka.kerub.data.ispn.utils

import com.github.K0zka.kerub.model.history.ChangeEvent
import nl.komponents.kovenant.Deferred
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent
import java.util.UUID

@Listener(observation = Listener.Observation.POST)
class CountdownCreateEventListener(
		private val deferred: Deferred<Unit, Exception>,
		private var countdown: Int = 1
) {
	@CacheEntryCreated
	@Synchronized
	fun listen(event: CacheEntryCreatedEvent<UUID, ChangeEvent>) {
		countdown--
		if (countdown == 0 && !deferred.promise.isDone()) {
			deferred.resolve(Unit)
		}
	}

}