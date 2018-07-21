package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.UUID
import java.util.UUID.randomUUID

class HostDynamicDaoImplTest : AbstractIspnDaoTest<UUID, HostDynamic>() {

	@Test
	fun listRunningHosts() {
		HostDynamicDaoImpl(cache!!, mock(), mock()).apply {
			val hostDynamic = HostDynamic(id = randomUUID(), status = HostStatus.Up)
			add(hostDynamic)

			assertEquals(listOf(hostDynamic.id), listRunningHosts(listOf(hostDynamic.id)))
			assertEquals(listOf(hostDynamic.id), listRunningHosts(listOf(hostDynamic.id, randomUUID())))
		}
	}
}