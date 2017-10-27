package com.github.kerubistan.kerub.data.dynamic

import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import java.util.UUID

interface HostDynamicDao : DynamicDao<HostDynamic>

inline fun HostDynamicDao.doWithDyn(
		id: UUID,
		crossinline action: (HostDynamic) -> HostDynamic) {
	this.doWithDyn(id = id,
			blank = { HostDynamic(id = id, status = HostStatus.Up) },
			action = action
	)
}
