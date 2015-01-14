package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.AuditEntryDao
import com.github.K0zka.kerub.model.AuditEntry
import java.util.UUID
import org.infinispan.Cache
import org.infinispan.AdvancedCache
import org.infinispan.query.Search

public class AuditEntryDaoImpl(protected val cache : AdvancedCache<UUID, AuditEntry>) : AuditEntryDao{
	override fun add(entry: AuditEntry): UUID {
		cache.putAsync(entry.id, entry)
		return entry.id
	}
	override fun listById(id: UUID): List<AuditEntry> {
		return Search.getQueryFactory(cache)!!
				.from(javaClass<AuditEntry>())!!
				.build()!!
				.list<AuditEntry>()!!
				.toList()
	}
}