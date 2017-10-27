package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor

class DisableKsmExecutor(
		exec: HostCommandExecutor,
		hostDynDao: HostDynamicDao) : AbstractKsmExecutor<DisableKsm>(exec, hostDynDao, false)