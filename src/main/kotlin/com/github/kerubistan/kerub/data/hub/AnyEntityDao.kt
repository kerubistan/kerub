package com.github.kerubistan.kerub.data.hub

import com.github.kerubistan.kerub.model.Entity
import java.util.UUID
import kotlin.reflect.KClass

interface AnyEntityDao {
	fun get(clazz: KClass<out Entity<*>>, id: UUID): Entity<UUID>?
	fun getAll(clazz: KClass<out Entity<*>>, ids: Collection<UUID>): List<Entity<*>>
}