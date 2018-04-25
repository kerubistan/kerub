package com.github.kerubistan.kerub.planner.steps

interface InvertableStep :  AbstractOperationalStep {
	val inverseMatcher : (AbstractOperationalStep) -> Boolean
}