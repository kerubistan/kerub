package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.ControllerDao
import com.nhaarman.mockito_kotlin.mock
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.remoting.transport.Address
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class ControllerDaoImplTest {
	var dao : ControllerDao? = null
	val cacheManager : EmbeddedCacheManager = mock()
	val address1 : Address = mock()
	val address2 : Address = mock()


	@Before fun setup() {
		Mockito.`when`( cacheManager.members).thenReturn( listOf(address1, address1) )
		Mockito.`when`(address1.toString()).thenReturn("TEST-1")
		Mockito.`when`(address2.toString()).thenReturn("TEST-2")
		dao = ControllerDaoImpl(cacheManager)
	}

	@Test
	fun get() {
		assertEquals("TEST-1", dao!!.get("TEST-1") )
	}

	@Test fun list() {
		assertEquals(2, dao!!.list().size)
	}
}