package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.model.controller.AssignmentType
import org.infinispan.Cache
import org.infinispan.query.Search
import java.util.UUID

public class AssignmentDaoImpl(cache: Cache<UUID, Assignment>, eventListener: EventListener)
: AssignmentDao, ListableIspnDaoBase<Assignment, UUID>(cache, eventListener) {

	override fun listByControllerAndType(controller: String, type: AssignmentType): List<Assignment> {
		return basicSearch()
				.having("controller").eq(controller)
				.and()
				.having("type").eq(type)
				.list()
	}

	override fun listByController(controller: String): List<Assignment> {
		return basicSearch()
				.having("controller").eq(controller)
				.list()
	}

	private fun basicSearch() = Search.getQueryFactory(cache)
			.from(Assignment::class.java)

	override fun getEntityClass(): Class<Assignment> {
		return Assignment::class.java
	}
}