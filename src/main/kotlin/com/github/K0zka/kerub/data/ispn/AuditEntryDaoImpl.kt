package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AuditEntry
import com.github.K0zka.kerub.model.UpdateEntry
import org.infinispan.AdvancedCache
import org.infinispan.query.Search
import org.infinispan.query.dsl.Query
import java.util.UUID

class AuditEntryDaoImpl(protected val cache: AdvancedCache<UUID, AuditEntry>) : AuditEntryDao {
	override fun add(entry: AuditEntry): UUID {
		cache.putAsync(entry.id, entry)
		return entry.id
	}

	override fun listById(id: UUID): List<AuditEntry> {
		return Search.getQueryFactory(cache)!!
				.from(UpdateEntry::class.java)!!
				.having("idStr").eq(id.toString()).toBuilder<Query>()
				.build()!!
				.list<AuditEntry>() as List<AuditEntry>
	}
}