package com.github.kerubistan.kerub.planner.costs

import com.github.kerubistan.kerub.model.ExpectationLevel
import java.util.Comparator

object ViolationComparator : Comparator<Violation> {
	override fun compare(first: Violation, second: Violation) =
			ExpectationLevel.comparator.compare(first.expectation.level, second.expectation.level)
}