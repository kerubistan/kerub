package com.github.K0zka.kerub.host

/**
 * Represents a host's power managemet interface (therefore an instance is somehow connected to a host)
 */
public interface PowerManager {
	/**
	 * Power on the host
	 */
	fun on()

	/**
	 * Poser off the host
	 */
	fun off()
}