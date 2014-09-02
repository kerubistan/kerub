package com.github.K0zka.kerub.data.ispn

import java.util.UUID
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.data.HostDao
import org.infinispan.Cache
import org.infinispan.query.Search

public class HostDaoImpl(cache: Cache<UUID, Host>) : ListableIspnDaoBase<Host, UUID>(cache), HostDao {
	override fun getEntityClass(): Class<Host> {
		return javaClass<Host>()
	}
}