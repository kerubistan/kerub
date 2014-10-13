package com.github.K0zka.kerub.data.ispn

import java.util.UUID
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.data.HostDao
import org.infinispan.Cache
import org.infinispan.query.Search
import com.github.K0zka.kerub.data.EventListener

public class HostDaoImpl(cache: Cache<UUID, Host>, eventListener : EventListener) : ListableIspnDaoBase<Host, UUID>(cache, eventListener), HostDao {
	override fun getEntityClass(): Class<Host> {
		return javaClass<Host>()
	}
}