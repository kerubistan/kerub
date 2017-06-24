package com.github.K0zka.kerub.model.controller.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.utils.emptyString

/**
 * Global configuration shared across the kerub cluster.
 */
@JsonTypeName("config")
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
		val powerManagementEnabled: Boolean = true,
		/**
		 * Can use wake-on-lan + ssh to power on/off hosts.
		 */
		val wakeOnLanEnabled: Boolean = false,
		/**
		 * The controllers can enable/disable KSM.
		 */
		val ksmEnabled: Boolean = true,
		/**
		 * The controller can install software (monitoring, hypervisor, storage, etc) on the host as needed.
		 */
		val installSoftwareEnabled: Boolean = false,
		/**
		 * Storage-related technologies all here
		 */
		val storageTechnologies: StorageTechnologiesConfig = StorageTechnologiesConfig(),
		/**
		 * All hypervisor-related here.
		 */
		val hypervisorTechnologies: HypervisorTechnologies = HypervisorTechnologies()
) : Entity<String> {
	@JsonIgnore
	override val id: String = emptyString
}