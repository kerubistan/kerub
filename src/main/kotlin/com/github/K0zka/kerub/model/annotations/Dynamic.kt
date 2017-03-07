package com.github.K0zka.kerub.model.annotations

import com.github.K0zka.kerub.model.Entity
import kotlin.reflect.KClass

annotation class Dynamic(val value: KClass<out Entity<*>>)