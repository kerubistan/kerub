package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor

public class EnableKsmExecutor(exec: HostCommandExecutor,
                               hostDynDao: HostDynamicDao) : AbstractKsmExecutor<EnableKsm>(exec, hostDynDao, true)