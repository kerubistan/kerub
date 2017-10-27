package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.data.dynamic.ControllerDynamicDao
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.hamcrest.CoreMatchers
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.remoting.transport.Address
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class ControllerManagerImplTest {

	val dao: ControllerDynamicDao = mock()

	val cacheManager: EmbeddedCacheManager = mock()

	var address: Address = mock()

	@Test
	fun getControllerId() {
		Mockito.`when`(cacheManager.address).thenReturn(address)
		Mockito.`when`(address.toString()).thenReturn("PASS")
		Assert.assertThat(ControllerManagerImpl(dao, cacheManager).getControllerId(), CoreMatchers.`is`("PASS"))
	}

	@Test
	fun start() {
		Mockito.`when`(cacheManager.address).thenReturn(address)
		val notNullInstance = ControllerDynamic("", 1, 0, listOf())
		Mockito.`when`(dao.add(any<ControllerDynamic>()))
				.then {
					val controllerDynamic = it.arguments[0]!! as ControllerDynamic
					//checks
					""
				}
		ControllerManagerImpl(dao, cacheManager).start()
		Mockito.verify(dao)?.add(any<ControllerDynamic>())
	}
}