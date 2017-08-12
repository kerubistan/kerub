package com.github.K0zka.kerub.planner.steps.host.recycle

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.testHost
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class RecycleHostExecutorTest {
	@Test
	fun execute() {
		val hostDao = mock<HostDao>()
		val dynamicDao = mock<HostDynamicDao>()
		RecycleHostExecutor(hostDao, dynamicDao).execute(RecycleHost(testHost))

		verify(hostDao).remove(testHost.id)
		verify(dynamicDao).remove(testHost.id)
	}

}