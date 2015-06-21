package com.github.K0zka.kerub.host

import org.junit.Test
import com.github.k0zka.finder4j.backtrack.BacktrackService
import org.junit.Before
import org.junit.After
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.Mock
import com.github.K0zka.kerub.data.ControllerDao
import org.mockito.Mockito
import com.github.K0zka.kerub.model.Host
import kotlin.test.assertTrue
import com.github.K0zka.kerub.data.dynamic.ControllerDynamicDao
import com.github.K0zka.kerub.model.dynamic.ControllerDynamic

RunWith(MockitoJUnitRunner::class)
public class ControllerAssignerImplTest {
	var backtrack : BacktrackService? = null

	Mock
	var controllerDao : ControllerDao? = null

	Mock
	var controllerDynamicDao : ControllerDynamicDao? = null

	Before
	fun setup() {
		backtrack = BacktrackService()
		Mockito.`when`(controllerDynamicDao?.listAll()).thenReturn(listOf(
				ControllerDynamic(
						controllerId = "ctrl-1",
				        addresses = listOf(),
				        maxHosts = 64,
				        totalHosts = 0
				                 ),
				ControllerDynamic(
						controllerId = "ctrl-2",
						addresses = listOf(),
						maxHosts = 64,
						totalHosts = 10
				                 )

		                                                                 ))
	}

	After
	fun cleanup() {
		backtrack?.stop()
	}

	Test
	fun assignControllers() {
		val host1 = Host(address = "127.0.0.1", dedicated = true, publicKey = "")
		val host2 = Host(address = "127.0.0.2", dedicated = true, publicKey = "")
		val result = ControllerAssignerImpl(backtrack!!, controllerDao!!, controllerDynamicDao!!)
				.assignControllers(listOf(host1, host2))

		assertTrue(result.keySet().containsAll(listOf(host1, host2)))
		assertTrue(listOf("ctrl-1", "ctrl-2").containsAll(result.values()))
	}
}