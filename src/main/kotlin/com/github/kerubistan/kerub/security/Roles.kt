package com.github.kerubistan.kerub.security

const val admin = "admin"
const val user = "user"

/**
 * The list of application roles.
 * See permissions assigned to roles in roles.ini
 */
enum class Roles(val roleName: String) {
	/**
	 * The administrators are able to manage the infrastructure, manage hosts, storage, networks.
	 */
	Admin(admin),
	/**
	 * Users can use the virtual machines created for them, but they can not create new resources.
	 */
	User(user)
}