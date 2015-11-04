package com.github.K0zka.kerub.planner.costs

import java.util.Comparator

public object RiskComparator : Comparator<Risk> {
	override fun compare(first: Risk, second: Risk): Int
			= first.score - second.score
}