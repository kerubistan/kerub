package com.github.K0zka.kerub.data.ispn

import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.Mock
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.ControllerDao
import org.junit.Before
import org.infinispan.manager.DefaultCacheManager
import org.springframework.cache.CacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import org.junit.After
import org.mockito.Mockito
import org.infinispan.remoting.transport.Address
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
public class ControllerDaoImplTest {
	var dao : ControllerDao? = null
	@Mock
	var cacheManager : EmbeddedCacheManager? = null
	@Mock
	var address1 : Address? = null
	@Mock
	var address2 : Address? = null


	@Before fun setup() {
		Mockito.`when`( cacheManager!!.getMembers() ).thenReturn( listOf(address1, address1) )
		Mockito.`when`(address1.toString()).thenReturn("TEST-1")
		Mockito.`when`(address2.toString()).thenReturn("TEST-2")
		dao = ControllerDaoImpl(cacheManager!!)
	}

	@Test
	fun get() {
		assertEquals("TEST-1", dao!!.get("TEST-1") )
	}

	@Test fun list() {
		assertEquals(2, dao!!.list().size)
	}
}