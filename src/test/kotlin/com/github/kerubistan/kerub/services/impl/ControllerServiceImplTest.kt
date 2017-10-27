package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.ControllerDao
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class ControllerServiceImplTest {
	val dao: ControllerDao = mock()

	@Test
	fun list() {
		whenever(dao.list()).thenReturn(listOf())
		ControllerServiceImpl(dao).list()
		verify(dao).list()
	}
}