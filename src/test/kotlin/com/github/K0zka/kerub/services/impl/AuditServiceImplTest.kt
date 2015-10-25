package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AuditEntry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
public class AuditServiceImplTest {
	@Mock
	var dao : AuditEntryDao? = null

	@Test
	fun listById() {
		Mockito.`when`(dao!!.listById(Matchers.any(javaClass<UUID>())?:UUID.randomUUID()))!!
			.thenReturn(listOf(AuditEntry(user = null)))
		val service = AuditServiceImpl(dao!!)
		val list = service.listById(UUID.randomUUID())
		Assert.assertEquals(1, list.size())
	}
}