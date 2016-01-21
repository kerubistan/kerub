package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageLink

fun storageToXml(disks : List<VirtualStorageLink>) : String {
	val ret = StringBuilder()
	for(disk in disks) {
		ret.append("""
		<disk type='file' device='disk'>
            <driver />
            <target dev='sda' bus='sata'/>
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
    </features>
    <devices>
		<emulator>/usr/bin/qemu-kvm</emulator>
		<input type='keyboard' bus='ps2'/>
		<graphics type='spice' autoport='yes'>
			<image compression='off'/>
		</graphics>
		<video>
			<model type='qxl' ram='65536' vram='65536' vgamem='16384' heads='1'/>
			<address type='pci' domain='0x0000' bus='0x00' slot='0x02' function='0x0'/>
		</video>
		${storageToXml(vm.virtualStorageLinks)}
    </devices>
</domain>
"""
}