package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.steps.host.powerdown.PowerDownHostFactory
import com.github.K0zka.kerub.planner.steps.vm.migrate.MigrateVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vm.stop.StopVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vstorage.migrate.MigrateVirtualStorageDeviceFactory
import com.github.k0zka.finder4j.backtrack.StepFactory
import java.util.ArrayList

public object CompositeStepFactory : StepFactory<AbstractOperationalStep, Plan> {

	val factories
			= listOf(MigrateVirtualMachineFactory, MigrateVirtualStorageDeviceFactory, PowerDownHostFactory,
			         StartVirtualMachineFactory, StopVirtualMachineFactory)

	override fun produce(state: Plan): List<AbstractOperationalStep> {
		val list = ArrayList<AbstractOperationalStep>()
		factories.forEach { list.addAll(it.produce(state.state)) }
		return list
	}

}