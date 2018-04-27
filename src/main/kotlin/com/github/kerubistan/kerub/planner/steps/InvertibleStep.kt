package com.github.kerubistan.kerub.planner.steps

interface InvertibleStep : AbstractOperationalStep {
	fun isInverseOf(other: AbstractOperationalStep): Boolean
}