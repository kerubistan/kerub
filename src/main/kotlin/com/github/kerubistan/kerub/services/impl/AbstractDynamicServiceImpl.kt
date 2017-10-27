package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.DaoOperations
import com.github.kerubistan.kerub.model.dynamic.DynamicEntity
import com.github.kerubistan.kerub.services.DynamicService
import java.util.UUID

abstract class AbstractDynamicServiceImpl<T : DynamicEntity>(
		private val dao: DaoOperations.Read<T, UUID>,
		private val entyityType: String
) : DynamicService<T> {
	override fun getById(id: UUID): T =
			assertExist(entyityType, dao.get(id), id)
}