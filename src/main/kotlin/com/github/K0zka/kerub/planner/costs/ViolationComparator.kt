package com.github.K0zka.kerub.planner.costs

import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.Comparator

object ViolationComparator : Comparator<Violation> {
	override fun compare(first: Violation, second: Violation) =
			ExpectationLevel.comparator.compare(first.expectation.level, second.expectation.level)
}