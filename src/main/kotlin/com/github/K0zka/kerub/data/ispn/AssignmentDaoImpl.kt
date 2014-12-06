package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.AssignmentDao
import java.util.UUID
import com.github.K0zka.kerub.model.controller.Assignment
import org.infinispan.Cache
import com.github.K0zka.kerub.data.EventListener
import org.infinispan.query.Search
import org.infinispan.query.dsl.Query

public class AssignmentDaoImpl(cache : Cache<UUID, Assignment>, eventListener : EventListener)
: AssignmentDao, ListableIspnDaoBase<Assignment, UUID>(cache, eventListener) {
	override fun listByController(controller: String) : List<Assignment> {
		return Search.getQueryFactory(cache)
				.from(javaClass<Assignment>())
				.having("controller")
				.eq(controller)
				.toBuilder<Query>()
				.build()
				.list<Assignment>()
	}

	override fun getEntityClass(): Class<Assignment> {
		return javaClass<Assignment>()
	}
}