package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.controller.Assignment
import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.Query
import java.util.UUID

public class AssignmentDaoImpl(cache : Cache<UUID, Assignment>, eventListener : EventListener)
: AssignmentDao, ListableIspnDaoBase<Assignment, UUID>(cache, eventListener) {
	override fun listByController(controller: String) : List<Assignment> {
		return Search.getQueryFactory(cache)
				.from(Assignment::class.java)
				.having("controller")
				.eq(controller)
				.toBuilder<Query>()
				.build()
				.list<Assignment>() as List<Assignment>
	}

	override fun getEntityClass(): Class<Assignment> {
		return Assignment::class.java
	}
}