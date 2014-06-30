package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import org.infinispan.Cache
import java.util.UUID
import com.github.K0zka.kerub.data.EventListener

public class HostDynamicDaoImpl(cache: Cache<UUID, HostDynamic>, eventListener: EventListener) : IspnDaoBase<HostDynamic, UUID>(cache, eventListener), HostDynamicDao {
}