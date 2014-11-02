package com.github.K0zka.kerub.security

/**
 * The list of application roles.
 * See permissions assigned to roles in roles.ini
 */
public enum class Roles(val name: String) {
	public class object {
		val admin = "admin"
		val powerUser = "poweruser"
		val user = "user"
	}
	/**
	 * The administrators are able to manage the infrastructure, manage hosts, storage, networks.
	 */
	Admin : Roles(admin)
	/**
	 * Power users are allowed to create virtual resources, including virtual machines, networks and disks.
	 */
	PowerUser : Roles(powerUser)
	/**
	 * Users can use the virtual machines created for them, but they can not create new resources.
	 */
	User : Roles(user)
}