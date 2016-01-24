package com.github.K0zka.kerub.planner.steps.vm.cpuaffinity

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object SetCpuAffinityFactory : AbstractOperationalStepFactory<SetCpuAffinity>() {
    override fun produce(state: OperationalState): List<SetCpuAffinity> {
        throw UnsupportedOperationException()
    }
}