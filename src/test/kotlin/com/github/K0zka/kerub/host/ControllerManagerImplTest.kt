package com.github.K0zka.kerub.host

import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.Mock
import com.github.K0zka.kerub.data.dynamic.ControllerDynamicDao
import org.infinispan.manager.EmbeddedCacheManager
import org.junit.Test
import org.mockito.Mockito
import org.infinispan.remoting.transport.Address
import org.mockito.Matchers
import com.github.K0zka.kerub.model.dynamic.ControllerDynamic
import org.junit.Assert
import com.github.K0zka.kerub.matchAny

RunWith(MockitoJUnitRunner::class)
public class ControllerManagerImplTest {

	Mock
	var dao : ControllerDynamicDao? = null

	Mock
	var cacheManager : EmbeddedCacheManager? = null

	Mock
	var address : Address? = null

	Test
	fun start() {
		Mockito.`when`(cacheManager?.getAddress()).thenReturn(address)
		val notNullInstance = ControllerDynamic("", 1, 0, listOf())
		Mockito.`when`(dao?.add(matchAny(javaClass<ControllerDynamic>(), notNullInstance)))
			.then {
				val controllerDynamic = it.getArguments()[0]!! as ControllerDynamic
				//checks
				""
			}
		ControllerManagerImpl(dao!!, cacheManager!!).start()
		Mockito.verify(dao)?.add(matchAny(javaClass<ControllerDynamic>(), notNullInstance))
	}
}