package com.github.kerubistan.kerub.model.annotations

import com.github.kerubistan.kerub.model.Entity
import kotlin.reflect.KClass

annotation class Dynamic(val value: KClass<out Entity<*>>)