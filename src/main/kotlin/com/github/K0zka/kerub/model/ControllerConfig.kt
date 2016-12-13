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
		val accountsRequired: Boolean = false,
		/**
		 * If power management is enabled, the controllers may power off or on hosts as needed
		 * in order to save power.
		 */
		val powerManagementEnabled : Boolean = true,
		/**
		 * List of paths the controllers can use as file storage.
		 */
		val fsPathEnabled : List<String> = listOf(),
		/**
		 * The controllers can create logical volumes on vgs.
		 */
		val lvmCreateVolumeEnabled : Boolean = true,
		/**
		 * The controllers can create gvinum volumes.
		 */
		val gvinumCreateVolumeEnabled : Boolean = true,
		/**
		 * The controllers can enable/disable KSM.
		 */
		val ksmEnabled : Boolean = true,
		/**
		 * The controller can install software (monitoring, hypervisor, storage, etc) on the host as needed.
		 */
		val installSoftwareEnabled : Boolean = false

) : Entity<String> {
	@JsonIgnore
	override val id: String = emptyString
}