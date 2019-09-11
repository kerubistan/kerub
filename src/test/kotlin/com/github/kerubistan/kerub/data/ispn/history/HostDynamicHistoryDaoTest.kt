package com.github.kerubistan.kerub.data.ispn.history

import com.github.kerubistan.kerub.data.ispn.AbstractIspnDaoTest
import com.github.kerubistan.kerub.data.ispn.utils.CountdownCreateEventListener
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.history.HistoryEntry
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.MB
import io.github.kerubistan.kroki.time.now
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.then
import org.junit.Ignore
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HostDynamicHistoryDaoTest : AbstractIspnDaoTest<UUID, HistoryEntry>() {

	@Test
	fun log() {
		val deferred = deferred<Unit, Exception>()
		cache!!.addListener(CountdownCreateEventListener(deferred))
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
	@Ignore("fails randomly, needs work")
	fun compress() {
		val deferred = deferred<Unit, Exception>()
		cache!!.addListener(CountdownCreateEventListener(deferred))
		val dao = HostDynamicHistoryDao(cache!!)
		val startTime = now()
		dao.log(
				HostDynamic(
						id = testHost.id,
						status = HostStatus.Up,
						ksmEnabled = false,
						memFree = 129.MB,
						storageStatus = listOf(
								SimpleStorageDeviceDynamic(
										id = testFsCapability.id,
										freeCapacity = 600.GB
								)
						)
				),
				HostDynamic(
						id = testHost.id,
						status = HostStatus.Up,
						ksmEnabled = true,
						memFree = 128.MB,
						storageStatus = listOf(
								SimpleStorageDeviceDynamic(
										id = testFsCapability.id,
										freeCapacity = 580.GB
								)
						)
				)
		)
		dao.log(
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = true, memFree = 128.MB),
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = false, memFree = 126.MB)
		)
		deferred.promise.then {
			val endTime = now()
			dao.compress(startTime, endTime, listOf(testHost.id))
		}.get()

	}

	@Test
	fun history() {
		val deferred = deferred<Unit, Exception>()
		cache!!.addListener(CountdownCreateEventListener(deferred))
		val dao = HostDynamicHistoryDao(cache!!)
		val startTime = now()
		dao.log(
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = false, memFree = 129.MB),
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = true, memFree = 128.MB)
		)
		dao.log(
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = true, memFree = 128.MB),
				HostDynamic(id = testHost.id, status = HostStatus.Up, ksmEnabled = false, memFree = 126.MB)
		)
		val history = deferred.promise.then {
			val endTime = now()
			dao.history(startTime - 10, endTime + 10, testHost.id)
		}.get()

		assertEquals(2, history.size)
	}

}