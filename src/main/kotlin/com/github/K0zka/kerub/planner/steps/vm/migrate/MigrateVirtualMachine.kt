package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.model.Constrained
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.NoMigrationExpectation
import com.github.K0zka.kerub.model.expectations.NotSameHostExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.ComputationCost
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.NetworkCost
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import java.math.BigInteger
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.minus
import kotlin.math.plus

public data class MigrateVirtualMachine(
		val vm: VirtualMachine,
		val source: Host,
		val target: Host) : AbstractOperationalStep {

	override fun reservations(): List<Reservation<*>>
			= listOf(VmReservation(vm),
			UseHostReservation(target),
			UseHostReservation(source)
			)

	override fun violations(state: OperationalState): Map<Constrained<Expectation>, List<Expectation>> {
		/**
		 * TODO
		 */
		val ret = HashMap<Constrained<Expectation>, List<Expectation>>()
		val vmViolations = ArrayList<Expectation>()
		for(expectation in vm.expectations) {
			when(expectation) {
				is NoMigrationExpectation ->
						vmViolations.add(expectation)
				is NotSameHostExpectation ->
						if(state.vmsOnHost(target.id).any { expectation.otherVmIds.contains(it.id) }) {
							vmViolations.add(expectation)
						}
				//TODO: and so on
			}
		}
		return ret
	}

	override fun take(state: OperationalState): OperationalState {
		val vmDyn = state.vmDyns[vm.id]!!
		val targetHostDyn = state.hostDyns[target.id]!!
		val sourceHostDyn = state.hostDyns[source.id]!!
		return state.copy(
				vmDyns = state.vmDyns +
						(vm.id to vmDyn.copy(
								hostId = target.id
						                    )),
		        hostDyns = state.hostDyns
				        + (target.id to targetHostDyn.copy(
						        memFree = targetHostDyn.memFree!! - vmDyn.memoryUsed,
				                memUsed = targetHostDyn.memUsed ?: BigInteger.ZERO + vmDyn.memoryUsed
				                                        ))
				        + (source.id to sourceHostDyn.copy(
					        memFree = sourceHostDyn.memFree!! + vmDyn.memoryUsed,
					        memUsed = sourceHostDyn.memUsed ?: BigInteger.ZERO - vmDyn.memoryUsed
				                                          ))
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
					bytes = vm.memory.max.longValue()
			           ),
			ComputationCost(
					host = target,
					cycles = vm.memory.max.longValue()
			               ),
			ComputationCost(
					host = source,
					cycles = vm.memory.max.longValue()
			               )

	                                           )
}