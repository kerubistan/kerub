package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.StorageDevice
import com.github.K0zka.kerub.model.VirtualMachine

fun storageToXml(disks : List<StorageDevice>) : String {
	val ret = StringBuilder()
	for(disk in disks) {
		ret.append("""
		<disk type='file'>
		</disk>
""")
	}
	return ret.toString()
}

fun escapeXmlText(str : String) : String {
	return str.replaceAll("<","&lt;").replaceAll(">","&gt;")
}

fun vmDefinitiontoXml(vm : VirtualMachine) : String {
return """
<domain>
    <name>${escapeXmlText(vm.name)}</name>
    <uuid>${vm.id}</uuid>
    <memory unit='KiB'>${vm.memoryMb.min * 1024}</memory>
    <vcpu>1</vcpu>
    <os>
        <smbios mode='sysinfo'/>
        <boot dev='hd'/>
        <boot dev='cdrom'/>
    </os>
    <features>
        <acpi/>
        <apic/>
        <pae/>
    </features>
    <devices>
		<emulator>/usr/bin/qemu-kvm</emulator>
        ${storageToXml(vm.storageDevices)}
    </devices>
</domain>
"""
}