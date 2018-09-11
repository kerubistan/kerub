package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.data.dynamic.ControllerDynamicDao
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.remoting.transport.Address
import org.junit.Assert.assertThat
import org.junit.Test

class ControllerManagerImplTest {

	private val dao: ControllerDynamicDao = mock()
	private val cacheManager: EmbeddedCacheManager = mock()
	private var address: Address = mock()

	@Test
	fun getControllerId() {
		whenever(cacheManager.address).thenReturn(address)
		whenever(address.toString()).thenReturn("PASS")
		assertThat(ControllerManagerImpl(dao, cacheManager).getControllerId(), CoreMatchers.`is`("PASS"))
	}

	@Test
	fun start() {
		whenever(cacheManager.address).thenReturn(address)
		val notNullInstance = ControllerDynamic("", 1, 0, listOf())
		whenever(dao.add(any()))
				.then {
					val controllerDynamic = it.arguments[0]!! as ControllerDynamic
					//checks
					""
				}
		ControllerManagerImpl(dao, cacheManager).start()
		verify(dao).add(any())
	}
}