package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.ControllerDao
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.remoting.transport.Address
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ControllerDaoImplTest {
	private var dao : ControllerDao? = null
	private val cacheManager : EmbeddedCacheManager = mock()
	private val address1 : Address = mock()
	private val address2 : Address = mock()


	@Before fun setup() {
		whenever(cacheManager.members).thenReturn( listOf(address1, address1) )
		whenever(address1.toString()).thenReturn("TEST-1")
		whenever(address2.toString()).thenReturn("TEST-2")
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