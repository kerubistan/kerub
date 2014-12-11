package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.model.Host

/**
 * Interface for the logic that decides which controller should connect the host.
 */
public trait ControllerAssigner {
	/**
	 * Convenience method to use when there is a single ost only.
	 * Delete if it never happens.
	 */
	fun assignController(host : Host) : String {
		return assignControllers(listOf(host))[host]!!
	}
	/**
	 * Return a map os host to controller assignments
	 */
	fun assignControllers(hosts : List<Host>) : Map<Host, String>
}