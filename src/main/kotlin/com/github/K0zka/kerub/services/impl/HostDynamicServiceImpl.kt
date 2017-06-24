package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.services.HostDynamicService

class HostDynamicServiceImpl(dao: HostDynamicDao)
	: AbstractDynamicServiceImpl<HostDynamic>(dao, "host-dynamic"), HostDynamicService