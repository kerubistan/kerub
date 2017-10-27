package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor

class EnableKsmExecutor(exec: HostCommandExecutor,
						hostDynDao: HostDynamicDao) : AbstractKsmExecutor<EnableKsm>(exec, hostDynDao, true)