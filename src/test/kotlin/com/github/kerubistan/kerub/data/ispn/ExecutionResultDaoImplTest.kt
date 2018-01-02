package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.ExecutionResult
import com.github.kerubistan.kerub.utils.now
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class ExecutionResultDaoImplTest : AbstractIspnDaoTest<UUID, ExecutionResult>() {

	@Test
	fun add() {
		ExecutionResultDaoImpl(cache!!).add(
				ExecutionResult(
						controllerId = "test",
						started = now(),
						steps = listOf()
				)
		)
	}

	@Test
	fun fieldSearch() {
		val executionResultDaoImpl = ExecutionResultDaoImpl(cache!!)
		val error = ExecutionResult(
				controllerId = "test",
				started = now(),
				steps = listOf()
		)
		executionResultDaoImpl.add(error)
		val list = executionResultDaoImpl.fieldSearch(field = ExecutionResult::controllerId.name, value = "test")
		assertEquals(listOf(error), list)
	}

	@Test
	fun count() {
		val executionResultDaoImpl = ExecutionResultDaoImpl(cache!!)
		val error = ExecutionResult(
				controllerId = "test",
				started = now(),
				steps = listOf()
		)
		executionResultDaoImpl.add(error)
		assertEquals(executionResultDaoImpl.count(), 1)
	}

}