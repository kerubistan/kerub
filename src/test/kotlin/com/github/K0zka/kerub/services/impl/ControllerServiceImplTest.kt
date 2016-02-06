package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.ControllerDao
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class) class ControllerServiceImplTest {
	@Mock
	var dao : ControllerDao? = null

	@Test
	fun list() {
		Mockito.`when`(dao!!.list()).thenReturn(listOf())
		ControllerServiceImpl(dao!!).list()
		Mockito.verify(dao!!).list()
	}
}