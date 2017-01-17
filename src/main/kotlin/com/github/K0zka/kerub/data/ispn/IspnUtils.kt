package com.github.K0zka.kerub.data.ispn

import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.FilterConditionContext
import org.infinispan.query.dsl.Query
import org.infinispan.query.dsl.QueryBuilder
import kotlin.reflect.KClass

fun <T> FilterConditionContext.list(): List<T> =
		this.toBuilder<Query>()
				.build()
				.list<T>()

fun <T> QueryBuilder<*>.list(): List<T> =
		this
				.build()
				.list<T>()

fun <T : Any, K, V> Cache<K, V>.queryBuilder(clazz : KClass<T>) :QueryBuilder<Query> =
		Search.getQueryFactory(this)
				.from(clazz.java)

fun <K, V : Any> Cache<K, V>.fieldSearch(
		type : KClass<V>,
		field: String,
		value: String,
		start: Long,
		limit: Int): List<V> =

		this.queryBuilder(type)
				.startOffset(start)
				.maxResults(limit)
				.having(field).like("%${value}%")
				.list()

fun <K, V : Any> Cache<K, V>.parallelStream()
		= this.advancedCache.cacheEntrySet().parallelStream()
