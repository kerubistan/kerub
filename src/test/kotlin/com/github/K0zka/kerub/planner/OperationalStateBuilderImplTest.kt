package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.ControllerManager
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
public class OperationalStateBuilderImplTest {

	@Mock
	var controllerManager: ControllerManager? = null
	@Mock
	val assignments: AssignmentDao? = null
	@Mock
	val hostDyn: HostDynamicDao? = null
	@Mock
	val hostDao: HostDao? = null

	@Test
	fun buildState() {
		Mockito.`when`(controllerManager?.getControllerId() ?: 0).thenReturn("TEST-CONTROLLER")
		Mockito.`when`(assignments?.listByController(Matchers.eq("TEST-CONTROLLER") ?: "")).thenReturn(listOf())

		val state = OperationalStateBuilderImpl(controllerManager!!, assignments!!, hostDyn!!, hostDao!!).buildState()

		Mockito.verify(controllerManager!!).getControllerId()
		Mockito.verify(assignments).listByController(Matchers.eq("TEST-CONTROLLER") ?: "")
	}
}