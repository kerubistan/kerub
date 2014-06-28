package com.github.K0zka.kerub.data.ispn

import java.util.UUID
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.data.HostDao
import org.infinispan.Cache

public class HostDaoImpl(cache : Cache<UUID, Host>) : IspnDaoBase<Host, UUID>(cache), HostDao {
	override fun listAll(): List<Host> {
		return cache.values().toList()
	}
}