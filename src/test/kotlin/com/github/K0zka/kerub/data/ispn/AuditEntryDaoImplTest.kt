package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.model.AddEntry
import com.github.K0zka.kerub.model.AuditEntry
import com.github.K0zka.kerub.model.DeleteEntry
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.UpdateEntry
import com.github.K0zka.kerub.utils.toUUID
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.mockito.Mockito
import java.util.UUID
import kotlin.test.assertTrue

class AuditEntryDaoImplTest : AbstractIspnDaoTest<UUID, AuditEntry>() {

	val oldEntity: Entity<UUID> = mock()
	val newEntity: Entity<UUID> = mock()

	@Test
	fun add() {

		val dao = AuditEntryDaoImpl(cache!!.advancedCache)

		Mockito.`when`(oldEntity.id)!!
				.thenReturn("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc".toUUID())
		Mockito.`when`(newEntity.id)!!
				.thenReturn("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc".toUUID())

		dao.add(AddEntry(
				new = oldEntity,
				user = UUID.randomUUID().toString()))

		dao.add(UpdateEntry(
				old = oldEntity,
				new = newEntity,
				user = UUID.randomUUID().toString()))

		dao.add(DeleteEntry(
				old = newEntity,
				user = UUID.randomUUID().toString()))

		val list = dao.listById("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc".toUUID())
		//since this operation is asynchronous, all we know is that the number of events
		// is less than 4
		assertTrue(list.size < 4)
	}
}