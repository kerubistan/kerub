package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Entity
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.whenever

fun <T : Entity<I>, I> CrudDao<T, I>.mockUpdate(id: I, entity: T) {
	doAnswer { mockInvocation ->
		(mockInvocation.arguments[2] as (T) -> T).invoke(entity)
	}.whenever(this).update(eq(id), retrieve = any(), change = any())
}

fun <T : Entity<I>, I> CrudDao<T, I>.mockUpdate(entity: T) {
	this.mockUpdate(entity.id, entity)
}

fun <I, T : Entity<I>> DaoOperations.Listen<I, T>.mockWaitAndGet(entity : T) {
	doAnswer {mockInvocation ->
		(mockInvocation.arguments[1] as (Any) -> Boolean).invoke(entity)
	}.whenever(this).waitFor(eq(entity.id), action = any())
}
