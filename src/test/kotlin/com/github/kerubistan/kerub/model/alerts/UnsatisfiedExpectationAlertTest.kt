package com.github.kerubistan.kerub.model.alerts

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.time.now
import java.util.UUID.randomUUID

internal class UnsatisfiedExpectationAlertTest : AbstractDataRepresentationTest<UnsatisfiedExpectationAlert>() {
	override val testInstances: Collection<UnsatisfiedExpectationAlert>
		get() = listOf(
				UnsatisfiedExpectationAlert(
						id = randomUUID(),
						created = now(),
						resolved = null,
						open = true,
						entityId = testVm.id,
						expectation = VirtualMachineAvailabilityExpectation(
								up = true
						)
				)
		)
	override val clazz = UnsatisfiedExpectationAlert::class.java

}