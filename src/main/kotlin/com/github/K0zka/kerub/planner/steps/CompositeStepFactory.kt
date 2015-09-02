package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalStateTransformation
import com.github.k0zka.finder4j.backtrack.StepFactory
import java.util.ArrayList

public object CompositeStepFactory : StepFactory<AbstractOperationalStep, OperationalStateTransformation> {

	val factories
			= listOf(MigrateVirtualMachineFactory, MigrateVirtualStorageDeviceFactory, PowerDownHostFactory,
			         StartVirtualMachineFactory, StopVirtualMachineFactory)

	override fun produce(state: OperationalStateTransformation): List<AbstractOperationalStep> {
		val list = ArrayList<AbstractOperationalStep>()
		//TODO factories.forEach { list.addAll(it.produce(state.state)) }
		return list
	}

}