package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.kerubistan.kerub.model.alerts.DataLossAlert
import com.github.kerubistan.kerub.model.alerts.HostLostAlert
import com.github.kerubistan.kerub.model.alerts.HostOverheatingAlert
import com.github.kerubistan.kerub.model.alerts.NetworkLinkDownAlert
import com.github.kerubistan.kerub.model.alerts.StorageFailureAlert
import com.github.kerubistan.kerub.model.alerts.UnsatisfiedExpectationAlert
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.controller.Assignment
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualNetworkDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.errors.HardwareError
import com.github.kerubistan.kerub.model.network.NetworkSwitch
import java.io.Serializable

/**
 * Generic entity type.
 * The only sure thing about an entity is that it has got an ID
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(Account::class),
		JsonSubTypes.Type(AccountMembership::class),
		JsonSubTypes.Type(Assignment::class),
		JsonSubTypes.Type(ControllerConfig::class),
		JsonSubTypes.Type(ControllerDynamic::class),
		JsonSubTypes.Type(Host::class),
		JsonSubTypes.Type(HostDynamic::class),
		JsonSubTypes.Type(Icon::class),
		JsonSubTypes.Type(VirtualMachine::class),
		JsonSubTypes.Type(VirtualNetwork::class),
		JsonSubTypes.Type(VirtualStorageDevice::class),
		JsonSubTypes.Type(Project::class),
		JsonSubTypes.Type(ProjectMembership::class),
		JsonSubTypes.Type(Pool::class),
		JsonSubTypes.Type(Template::class),
		JsonSubTypes.Type(Network::class),
		JsonSubTypes.Type(NetworkSwitch::class),
		JsonSubTypes.Type(AddEntry::class),
		JsonSubTypes.Type(DeleteEntry::class),
		JsonSubTypes.Type(UpdateEntry::class),
		JsonSubTypes.Type(Event::class),
		JsonSubTypes.Type(ExecutionResult::class),
		JsonSubTypes.Type(Violation::class),
		JsonSubTypes.Type(DataLossAlert::class),
		JsonSubTypes.Type(NetworkLinkDownAlert::class),
		JsonSubTypes.Type(StorageFailureAlert::class),
		JsonSubTypes.Type(HostLostAlert::class),
		JsonSubTypes.Type(HostConfiguration::class),
		JsonSubTypes.Type(HardwareError::class),
		JsonSubTypes.Type(HostOverheatingAlert::class),
		JsonSubTypes.Type(UnsatisfiedExpectationAlert::class),
		JsonSubTypes.Type(VirtualMachineDynamic::class),
		JsonSubTypes.Type(VirtualNetworkDynamic::class),
		JsonSubTypes.Type(VirtualStorageDeviceDynamic::class)
)
interface Entity<T> : Serializable {
	//	@DocumentId
	//	@JsonProperty("id")
	val id: T
	val idStr: String
		@JsonIgnore
		get() = id.toString()
}