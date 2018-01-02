package com.github.kerubistan.kerub.planner

interface ProblemDetector {
	fun detect() : Collection<Problem>
}