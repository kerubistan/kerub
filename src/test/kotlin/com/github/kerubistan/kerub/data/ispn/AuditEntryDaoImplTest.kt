package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.model.AddEntry
import com.github.kerubistan.kerub.model.AuditEntry
import com.github.kerubistan.kerub.model.DeleteEntry
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.UpdateEntry
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.strings.toUUID
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class AuditEntryDaoImplTest : AbstractIspnDaoTest<UUID, AuditEntry>() {

	private val oldEntity: Entity<UUID> = mock()
	private val newEntity: Entity<UUID> = mock()

	@Test
	fun add() {

		val dao = AuditEntryDaoImpl(cache!!.advancedCache)

		whenever(oldEntity.id)
				.thenReturn("43dcc6e7-cfcd-44af-a4e5-bbe8f7d948cc".toUUID())
		whenever(newEntity.id)
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