package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.utils.buildString
import com.github.K0zka.kerub.utils.storage.iscsiStorageId
import com.github.K0zka.kerub.utils.storage.iscsiDefaultUser as iscsiUser

fun storagesToXml(disks: List<VirtualStorageLinkInfo>, targetHost:Host): String {
	return buildString(disks.size * 256) {
		var targetDev = 'a'
		for (link in disks) {
			append(storageToXml(link, targetHost, targetDev))
			targetDev++
		}
	}
}

val allocationTypeToDiskType = mapOf(
		VirtualStorageFsAllocation:: class to "file",
		VirtualStorageLvmAllocation::class to "block"
)

private fun storageToXml(
		linkInfo: VirtualStorageLinkInfo, targetHost: Host, targetDev: Char): String {

	return """
		<disk type='${kvmDeviceType(linkInfo, targetHost)}' device='${linkInfo.link.device.name.toLowerCase()}'>
            <driver name='qemu' type='${allocationType(linkInfo.device.dynamic!!)}' cache='none'/>
            ${if(linkInfo.device.stat.readOnly || linkInfo.link.readOnly) "<readonly/>" else ""}
            ${allocationToXml(linkInfo, targetHost)}
            <target dev='sd$targetDev' bus='${linkInfo.link.bus}'/>
		</disk>
"""
}

private fun kvmDeviceType(linkInfo: VirtualStorageLinkInfo, targetHost: Host) : String {
	if(remoteHost(linkInfo, targetHost)) {
		return "network"
	} else {
		return allocationTypeToDiskType[requireNotNull(linkInfo.device.dynamic).allocation.javaClass.kotlin] ?: TODO()
	}
}

fun allocationType(deviceDyn: VirtualStorageDeviceDynamic): String {
	return when(deviceDyn.allocation) {
		is VirtualStorageLvmAllocation -> "raw"
		is VirtualStorageFsAllocation -> deviceDyn.allocation.type.name.toLowerCase()
		else -> TODO("")
	}
}

fun allocationToXml(linkInfo : VirtualStorageLinkInfo, targetHost: Host): String {
	return if(remoteHost(linkInfo, targetHost)) {
		"""
		<source protocol='iscsi' name='${iscsiStorageId(linkInfo.device.stat.id)}/1'>
			<host name='${linkInfo.storageHost.stat.address}' port='3260' />
			<auth username="$iscsiUser">
				<secret type="" />
			</auth>
		</source>
		"""
	} else {
		val allocation = requireNotNull(linkInfo.device.dynamic).allocation
		when(allocation) {
			is VirtualStorageFsAllocation ->
				"<source file='${allocation.mountPoint}/${linkInfo.device.stat.id}'/>"
			is VirtualStorageLvmAllocation ->
				"<source dev='${allocation.path}'/>"
			else -> TODO()
		}
	}
}

private fun remoteHost(linkInfo: VirtualStorageLinkInfo, targetHost: Host) = linkInfo.device.dynamic?.allocation?.hostId != targetHost.id

fun vmDefinitiontoXml(vm: VirtualMachine, disks: List<VirtualStorageLinkInfo>, password : String, targetHost : Host): String {
	return """
<domain type='kvm'>
    <name>${vm.id}</name>
    <uuid>${vm.id}</uuid>
    <memory unit='B'>${vm.memory.min}</memory>
    <vcpu>${vm.nrOfCpus}</vcpu>
    <os>
    	<type arch='x86_64'>hvm</type>
        <boot dev='hd'/>
        <boot dev='cdrom'/>
    </os>
    <features>
        <acpi/>
        <apic/>
        <pae/>
        <hap/>
    </features>
    <devices>
		<emulator>/usr/bin/qemu-kvm</emulator>
		<input type='keyboard' bus='ps2'/>
		<graphics type='spice' autoport='yes' listen='0.0.0.0' passwd='$password'>
			<listen type='address' address='0.0.0.0'/>
			<image compression='off'/>
		</graphics>
		<video>
			<model type='qxl' ram='65536' vram='65536' vgamem='16384' heads='1'/>
			<address type='pci' domain='0x0000' bus='0x00' slot='0x02' function='0x0'/>
		</video>
		${storagesToXml(disks, targetHost)}
    </devices>
</domain>
"""
}