package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.issues.problems.HostProblem

data class VmOnOldHost(
		/**
		 * A host with expired support, beyond planed lifecycle, etc, but still running workload.
		 */
		override val host: Host,
		/**
		 * The VM on the old host.
		 */
		val vm: VirtualMachine,
		/**
		 * Number of days since end of hardware lifecycle.
		 */
		val hostExpiredSince: Int
) : HostProblem