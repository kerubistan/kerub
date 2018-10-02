package com.github.kerubistan.kerub.planner.steps

import com.github.k0zka.finder4j.backtrack.StepFactory
import kotlin.reflect.KClass

annotation class ProducedBy(val factory: KClass<out StepFactory<*, *>>)
