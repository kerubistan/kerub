package com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.NoMigrationExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.ComputationCost
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.NetworkCost
import com.github.kerubistan.kerub.planner.costs.Risk
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.vm.migrate.MigrateVirtualMachine
import com.github.kerubistan.kerub.utils.any
import io.github.kerubistan.kroki.collections.update
import java.math.BigInteger

@JsonTypeName("kvm-migrate-vm")
data class KvmMigrateVirtualMachine(
		override val vm: VirtualMachine,
		override val source: Host,
		override val target: Host
) : AbstractOperationalStep, MigrateVirtualMachine, InvertibleStep {

	override fun isInverseOf(other: AbstractOperationalStep) =
			other is KvmMigrateVirtualMachine && other.vm == vm && other.source == target && other.target == source

	override fun reservations(): List<Reservation<*>> = listOf(
			VmReservation(vm),
			UseHostReservation(target),
			UseHostReservation(source)
	)

	override fun take(state: OperationalState): OperationalState {
		val vmDyn = requireNotNull(state.vms[vm.id]?.dynamic)
		val targetHostDyn = requireNotNull(state.hosts[target.id]?.dynamic)
		val sourceHostDyn = requireNotNull(state.hosts[source.id]?.dynamic)
		return state.copy(
				vms = state.vms.update(vm.id) {
					it.copy(
							dynamic = vmDyn.copy(
									hostId = target.id
							)
					)
				},
				hosts = state.hosts
						.update(target.id) {
							it.copy(
									dynamic = targetHostDyn.copy(
											memFree = sourceHostDyn.memFree!! + vmDyn.memoryUsed,
											memUsed = (sourceHostDyn.memUsed ?: BigInteger.ZERO - vmDyn.memoryUsed)
													.coerceAtLeast(BigInteger.ZERO)
									)
							)
						}
						.update(source.id) {
							it.copy(
									dynamic = sourceHostDyn.copy(
											memFree = sourceHostDyn.memFree!! + vmDyn.memoryUsed,
											memUsed = (sourceHostDyn.memUsed ?: BigInteger.ZERO - vmDyn.memoryUsed)
													.coerceAtLeast(BigInteger.ZERO)
									)
							)
						}
		)
	}

	override fun getCost(): List<Cost> = listOf(
			/*
			 * TODO
			 * This calculates cost based on the max, which is pessimistic
			 * rather than realistic.
			 */
			NetworkCost(
					hosts = listOf(source, target),
					bytes = vm.memory.max.toLong()
			),
			ComputationCost(
					host = target,
					cycles = vm.memory.max.toLong()
			),
			ComputationCost(
					host = source,
					cycles = vm.memory.max.toLong()
			)

	) + if (vm.expectations.any<NoMigrationExpectation>()) {
		// TODO: hmm, do I want to have this logic here? NoMigrationExpectationViolationDetector detects the problem
		listOf(Risk(1000, comment = "broken no-migration rule"))
	} else {
		listOf<Cost>()
	}
}