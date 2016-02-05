package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.DaoOperations
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import com.github.K0zka.kerub.services.DynamicService
import java.util.UUID

abstract class AbstractDynamicServiceImpl<T : DynamicEntity>(
		private val dao: DaoOperations.Read<T, UUID>,
		private val entyityType: String
) : DynamicService<T> {
	override fun getById(id: UUID): T =
			assertExist(entyityType, dao.get(id), id)
}