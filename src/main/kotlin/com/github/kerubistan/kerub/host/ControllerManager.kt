package com.github.kerubistan.kerub.host

/**
 * Interface to get information about the controller.
 * A kerub installation may be built of a cluster of several controllers.
 * A controller is responsible only for a set of hosts. In case a controller is lost,
 * another controller have to take over the hosts.
 */
interface ControllerManager {
	/**
	 * Returns the ID of the controller.
	 */
	fun getControllerId(): String
}