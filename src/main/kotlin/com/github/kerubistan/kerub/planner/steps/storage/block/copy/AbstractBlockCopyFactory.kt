package com.github.kerubistan.kerub.planner.steps.storage.block.copy

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

abstract class AbstractBlockCopyFactory<T : AbstractBlockCopy> : AbstractOperationalStepFactory<T>() {
	final override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf(CloneOfStorageExpectation::class)

	final override val problemHints: Set<KClass<out Problem>>
		get() = setOf()
}