package com.github.K0zka.kerub.data.hub

import com.github.K0zka.kerub.model.Asset
import java.util.UUID
import kotlin.reflect.KClass

interface AnyAssetDao {
	fun <T : Asset> get(clazz: KClass<T>, id: UUID): T
	fun <T : Asset> getAll(clazz: KClass<T>, ids: Collection<UUID>): List<T>
}