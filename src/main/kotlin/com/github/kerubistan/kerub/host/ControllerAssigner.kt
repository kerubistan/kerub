package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.model.Host

/**
 * Interface for the logic that decides which controller should connect the host.
 */
interface ControllerAssigner {
	/**
	 * Convenience method to use when there is a single ost only.
	 * Delete if it never happens.
	 */
	fun assignController(host: Host) = assignControllers(listOf(host))

	/**
	 * Return a map os host to controller assignments
	 */
	fun assignControllers(hosts: List<Host>)
}