package com.github.K0zka.kerub.data.hub

import com.github.K0zka.kerub.model.Entity
import java.util.UUID
import kotlin.reflect.KClass

interface AnyEntityDao {
	fun get(clazz: KClass<out Entity<*>>, id: UUID): Entity<UUID>?
	fun getAll(clazz: KClass<out Entity<*>>, ids: Collection<UUID>): List<Entity<*>>
}