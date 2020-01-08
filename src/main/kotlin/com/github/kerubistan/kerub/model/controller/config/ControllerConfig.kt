package com.github.kerubistan.kerub.model.controller.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.utils.emptyString

/**
 * Global configuration shared across the kerub cluster.
 */
@JsonTypeName("config")
data class ControllerConfig(
		/**
		 * The amount of time in milliseconds after which the controller should give up trying to satisfy a requirement.
		 */
		val plannerTimeout: Int? = null,

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
		 * The maximum redundancy level allowed by the controller.
		 */
		val maximumRedundancyAllowed: Short = 4,
		/**
		 * All hypervisor-related here.
		 */
		val hypervisorTechnologies: HypervisorTechnologies = HypervisorTechnologies(),
		/**
		 * Configuration for the history archiver
		 */
		val archiverConfig: ArchiverConfig = ArchiverConfig(),
		/**
		 * CPU temperature is considered normal between two limits.
		 * This applies to all processor types, while for example ARM CPUs are usually cooler even under high load.
		 */
		val cpuNormalTemperature: Range<Int> = Range(20, 70)
) : Entity<String> {
	@JsonIgnore
	override val id: String = emptyString
}