package com.github.kerubistan.kerub.model

/**
 * An interface marking the entities that represent persistent information on the hosts and therefore
 * needs cleanup operation before delete.
 */
interface Recyclable {
	/**
	 * Set to true when you no longer want to have it.
	 */
	val recycling: Boolean
}