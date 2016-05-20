package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.dynamic.VirtualStorageAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.utils.buildString

fun storagesToXml(disks: Map<VirtualStorageLink, Pair<VirtualStorageDevice, VirtualStorageDeviceDynamic> >): String {
	return buildString(disks.size * 256) {
		var targetDev = 'a'
		for (device in disks) {
			append(storageToXml(device.value.first, device.key, device.value.second.allocation, targetDev))
			targetDev++
		}
	}
}

val allocationTypeToDiskType = mapOf(
		VirtualStorageFsAllocation:: class to "file",
		VirtualStorageLvmAllocation::class to "block"
)

private fun storageToXml(disk : VirtualStorageDevice, link: VirtualStorageLink, allocation : VirtualStorageAllocation, targetDev : Char): String {
	return """
		<disk type='${allocationTypeToDiskType[allocation.javaClass.kotlin]}' device='${link.device.name.toLowerCase()}'>
            <driver />
            ${if(link.readOnly || disk.readOnly) "<readonly/>" else ""}
            ${allocationToXml(allocation, disk)}
            <target dev='sd$targetDev' bus='${link.bus}'/>
		</disk>
"""
}

fun allocationToXml(allocation: VirtualStorageAllocation, disk : VirtualStorageDevice): String {
	when(allocation) {
		is VirtualStorageFsAllocation -> return "<source file='${allocation.mountPoint}/${disk.id}'/>"
		is VirtualStorageLvmAllocation -> return "<source dev='${allocation.path}'/>"
		else -> return TODO()
	}
}

fun escapeXmlText(str: String): String {
	return str.replace("<".toRegex(), "&lt;").replace(">".toRegex(), "&gt;")
}

fun vmDefinitiontoXml(vm: VirtualMachine, disks: Map<VirtualStorageLink, Pair<VirtualStorageDevice, VirtualStorageDeviceDynamic> >, password : String): String {
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
		${storagesToXml(disks)}
    </devices>
</domain>
"""
}