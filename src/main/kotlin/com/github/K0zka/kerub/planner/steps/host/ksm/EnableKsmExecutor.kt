package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.planner.StepExecutor

public class EnableKsmExecutor(exec: HostCommandExecutor,
                               hostDynDao: HostDynamicDao) : AbstractKsmExecutor<EnableKsm>(exec, hostDynDao, true)