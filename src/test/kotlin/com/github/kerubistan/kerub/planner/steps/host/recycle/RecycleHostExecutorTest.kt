package com.github.kerubistan.kerub.planner.steps.host.recycle

import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.testHost
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