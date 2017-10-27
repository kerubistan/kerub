package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.services.HostDynamicService

class HostDynamicServiceImpl(dao: HostDynamicDao)
	: AbstractDynamicServiceImpl<HostDynamic>(dao, "host-dynamic"), HostDynamicService