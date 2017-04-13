package com.github.K0zka.kerub.data.ispn

import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.FilterConditionContext
import org.infinispan.query.dsl.Query
import org.infinispan.query.dsl.QueryBuilder
import org.infinispan.query.dsl.SortOrder
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

inline fun <reified K, reified V : Any> Cache<K, V>.fieldEq(
		field: String,
		value: String,
		start: Long,
		limit: Int): List<V> =
		this.fieldEq(V::class, field, value, start, limit)

fun <K, V : Any> Cache<K, V>.fieldEq(
		type : KClass<out V>,
		field: String,
		value: Any,
		start: Long,
		limit: Int): List<V> =
		this.queryBuilder(type)
				.startOffset(start)
				.maxResults(limit)
				.having(field).eq(value)
				.list()

inline fun <reified K, reified V : Any> Cache<K, V>.fieldSearch(
		field: String,
		value: String,
		start: Long,
		limit: Int): List<V> =
		this.fieldSearch(V::class, field, value, start, limit)

fun <K, V : Any> Cache<K, V>.fieldSearch(
		type : KClass<out V>,
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

inline fun <reified K, reified V> Cache<K, V>.search(
		start : Long = 0,
		limit : Int,
		order : SortOrder = SortOrder.DESC,
		sortProp : String) : List<V> =
		Search.getQueryFactory(this)
				.from(V::class.java)
				.orderBy(sortProp, order)
				.maxResults(limit)
				.startOffset(start)
				.list()
