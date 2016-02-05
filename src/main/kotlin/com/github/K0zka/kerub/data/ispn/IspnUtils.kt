package com.github.K0zka.kerub.data.ispn

import org.infinispan.query.dsl.FilterConditionContext
import org.infinispan.query.dsl.Query

fun <T> FilterConditionContext.list(): List<T> =
		this.toBuilder<Query>()
				.build()
				.list<T>()