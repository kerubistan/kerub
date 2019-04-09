package com.github.kerubistan.kerub.planner.steps.storage.remove

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class RemoveVirtualStorageTest : AbstractOperationalStepVerifications(RemoveVirtualStorage(testDisk)) {
	@Test
	fun take() {
		val state = OperationalState.fromLists(
				vms = listOf(testVm),
				hosts = listOf(testHost),
				vStorage = listOf(testDisk)
		)
		val updatedState = RemoveVirtualStorage(testDisk).take(state)
		assertTrue(updatedState.vStorage.isEmpty())
	}
}