package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.model.Entity

interface EntityAccessController {
	fun <T> checkAndDo(obj: Entity<*>, action: () -> T?): T?
}