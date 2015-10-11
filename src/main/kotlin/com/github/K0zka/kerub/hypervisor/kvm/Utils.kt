package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageLink
import kotlin.math.times

fun storageToXml(disks : List<VirtualStorageLink>) : String {
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
	return str.replace("<".toRegex(), "&lt;").replace(">".toRegex(), "&gt;")
}

fun vmDefinitiontoXml(vm : VirtualMachine) : String {
return """
<domain>
    <name>${escapeXmlText(vm.name)}</name>
    <uuid>${vm.id}</uuid>
    <memory unit='B'>${vm.memory.min}</memory>
    <vcpu>${vm.nrOfCpus}</vcpu>
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
        ${storageToXml(vm.virtualStorageLinks)}
    </devices>
</domain>
"""
}