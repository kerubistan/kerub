package com.github.K0zka.kerub.model.dynamic

/**
 * Handled statuses of a host
 */
enum class HostStatus {
	/**
	 * The host is being connected to the kerub controller.
	 */
	Connecting,
	/**
	 * The host is connected, it's capabilities are up to date.
	 */
	Up,
	/**
	 * The host is disconnected.
	 */
	Down,
	/**
	 * The host is booting up.
	 */
	Booting,
	ShuttingDown,
	Unavailable
}