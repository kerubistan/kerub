package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AddEntry
import com.github.K0zka.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class AuditServiceImplTest {
	val dao : AuditEntryDao = mock()

	@Test
	fun listById() {
		whenever(dao.listById(any()))
			.thenReturn(listOf(AddEntry(user = null, new = testHost)))
		val service = AuditServiceImpl(dao)
		val list = service.listById(UUID.randomUUID())
		assertEquals(1, list.size)
	}
}