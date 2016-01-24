package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor

public class DisableKsmExecutor(
		exec : HostCommandExecutor,
		hostDynDao : HostDynamicDao) : AbstractKsmExecutor<DisableKsm>(exec, hostDynDao, false)