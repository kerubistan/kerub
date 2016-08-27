package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AddEntry
import com.github.K0zka.kerub.model.AuditEntry
import com.github.K0zka.kerub.model.DeleteEntry
import com.github.K0zka.kerub.model.UpdateEntry
import org.infinispan.AdvancedCache
import org.infinispan.query.Search
import org.infinispan.query.dsl.Query
import org.infinispan.query.dsl.QueryBuilder
import java.util.UUID
import kotlin.reflect.KClass

class AuditEntryDaoImpl(protected val cache: AdvancedCache<UUID, AuditEntry>) : AuditEntryDao {
	override fun add(entity: AuditEntry): UUID {
		cache.putAsync(entity.id, entity)
		return entity.id
	}

	override fun listById(id: UUID): List<AuditEntry> =
			criteria(builder(AddEntry::class), id) +
					criteria(builder(UpdateEntry::class), id) +
					criteria(builder(DeleteEntry::class), id)

	private fun builder(clazz: KClass<*>) =
			Search.getQueryFactory(cache).from(clazz.java)

	private fun criteria(builder: QueryBuilder<*>, id: UUID): List<AuditEntry> =
			builder
					.having("idStr").eq(id.toString()).toBuilder<Query>()
					.build()!!
					.list<AuditEntry>()
}