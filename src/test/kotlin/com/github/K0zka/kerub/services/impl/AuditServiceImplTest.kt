package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AddEntry
import com.github.K0zka.kerub.model.AuditEntry
import com.github.K0zka.kerub.model.UpdateEntry
import com.github.K0zka.kerub.testHost
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

class AuditServiceImplTest {
	val dao : AuditEntryDao = mock()

	@Test
	fun listById() {
		Mockito.`when`(dao.listById(Matchers.any(UUID::class.java)?:UUID.randomUUID()))!!
			.thenReturn(listOf(AddEntry(user = null, new = testHost)))
		val service = AuditServiceImpl(dao)
		val list = service.listById(UUID.randomUUID())
		Assert.assertEquals(1, list.size)
	}
}