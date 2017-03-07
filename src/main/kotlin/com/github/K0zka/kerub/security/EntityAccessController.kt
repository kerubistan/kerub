package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.model.Entity

interface EntityAccessController {
	fun <T> checkAndDo(obj: Entity<*>, action: () -> T?): T?
}