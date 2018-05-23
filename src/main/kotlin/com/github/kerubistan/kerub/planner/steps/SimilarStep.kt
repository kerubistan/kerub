package com.github.kerubistan.kerub.planner.steps

import com.github.k0zka.finder4j.backtrack.Step
import com.github.kerubistan.kerub.planner.Plan

interface SimilarStep {
	fun isLikeStep(other : Step<Plan>) : Boolean
}