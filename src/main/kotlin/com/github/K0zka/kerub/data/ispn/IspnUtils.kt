package com.github.K0zka.kerub.data.ispn

import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.FilterConditionContext
import org.infinispan.query.dsl.Query
import kotlin.reflect.KClass

fun <T> FilterConditionContext.list(): List<T> =
		this.toBuilder<Query>()
				.build()
				.list<T>()

fun <K, V : Any> Cache<K, V>.fieldSearch(
		type : KClass<V>,
		field: String,
		value: String,
		start: Long,
		limit: Long): List<V> =

		Search.getQueryFactory(this)
				.from(type.java)
				.startOffset(start)
				.having(field).like("%${value}%").toBuilder<Query>()
				.maxResults(limit.toInt()).build()
				.list<V>() as List<V>

