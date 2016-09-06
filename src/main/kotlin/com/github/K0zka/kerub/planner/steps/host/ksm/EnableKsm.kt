package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.ComputationCost
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.reservations.FullHostReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.steps.vm.base.HostStep
import com.github.K0zka.kerub.utils.update

data class EnableKsm(override val host: Host, val cycles: Long) : HostStep {

	companion object {
		//TODO: issue #123 this depends on the operational state
		// size and number of virtual machines,
		// ther operating systems, etc
		// so basically this is just a placeholder
		val ksmGeneratedLoad = 5.toByte()
	}

	override fun reservations(): List<Reservation<*>>
			= listOf(FullHostReservation(host))

	override fun take(state: OperationalState): OperationalState {
		val dyn = requireNotNull(state.hosts[host.id]?.dynamic)

		return state.copy(
				hosts = state.hosts.update(host.id) {
					hostData ->
					hostData.copy(
							dynamic = dyn.copy(
									ksmEnabled = true,
									systemCpu = ((dyn.systemCpu ?: 0) + ksmGeneratedLoad).toByte(),
									idleCpu = ((dyn.idleCpu ?: 0) + ksmGeneratedLoad).toByte(),
									memFree = ((dyn.memFree))
							)
					)
				}
		)
	}

	override fun getCost(): List<Cost> {
		return listOf(ComputationCost(
				host = host,
				cycles = cycles
		)
		)
	}

}
