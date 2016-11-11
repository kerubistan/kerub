package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.utils.emptyString

/**
 * Global configuration shared across the kerub cluster.
 */
@JsonTypeName("controller-config")
data class ControllerConfig(
		/**
		 * If accounts are required, the controller allows creating virtual resources only in accounts
		 * Also users can only see the accounts/projects they are assigned to
		 */
		val accountsRequired: Boolean = false
) : Entity<String> {
	@JsonIgnore
	override val id: String = emptyString
}