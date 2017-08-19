package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.data.ispn.AbstractIspnDaoTest
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.history.ChangeEvent
import com.github.K0zka.kerub.model.history.HistoryEntry
import com.github.K0zka.kerub.testHost
import nl.komponents.kovenant.Deferred
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.then
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class HostDynamicHistoryDaoTest : AbstractIspnDaoTest<UUID, HistoryEntry>() {

	@Listener(observation = Listener.Observation.POST)
	class CreateEventListener(private val deferred: Deferred<Unit, Exception>) {
		@CacheEntryCreated
		fun listen(event: CacheEntryCreatedEvent<UUID, ChangeEvent>) {
			if (!deferred.promise.isDone()) {
				deferred.resolve(Unit)
			}
		}
	}

	@Test
	fun log() {
		val deferred = deferred<Unit, Exception>()
		cache!!.addListener(CreateEventListener(deferred))
		val dao = HostDynamicHistoryDao(cache!!)
		dao.log(
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = false),
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = true)
		)
		deferred.promise.then {
			assertTrue(cache!!.isNotEmpty())
			assertTrue(dao.list(testHost.id).isNotEmpty())
		}.get()
	}

	@Test
	fun compress() {
		val deferred = deferred<Unit, Exception>()
		cache!!.addListener(CreateEventListener(deferred))
		val dao = HostDynamicHistoryDao(cache!!)
		val startTime = System.currentTimeMillis()
		dao.log(
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = false),
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = true)
		)
		deferred.promise.then {
			val endTime = System.currentTimeMillis()
			dao.compress(startTime, endTime, listOf(testHost.id))
		}.get()

	}
}