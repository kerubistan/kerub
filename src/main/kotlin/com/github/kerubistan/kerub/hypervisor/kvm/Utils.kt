package com.github.kerubistan.kerub.hypervisor.kvm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageLinkInfo
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.utils.storage.iscsiStorageId
import io.github.kerubistan.kroki.io.readText
import io.github.kerubistan.kroki.xml.XmlBuilder
import io.github.kerubistan.kroki.xml.xml
import com.github.kerubistan.kerub.utils.storage.iscsiDefaultUser as iscsiUser

private const val file = "file"
private const val dev = "dev"
private const val type = "type"
private const val block = "block"
private const val source = "source"
private const val name = "name"
private const val uuid = "uuid"
private const val iscsi = "iscsi"
private const val unit = "unit"
private const val bytes = "B"
private const val boot = "boot"
private const val address = "address"
private const val allAddresses = "0.0.0.0"

val allocationTypeToDiskType = mapOf(
		VirtualStorageFsAllocation::class to file,
		VirtualStorageLvmAllocation::class to block
)

private fun kvmDeviceType(linkInfo: VirtualStorageLinkInfo, targetHost: Host): String =
		if (isRemoteHost(linkInfo, targetHost)) {
			when (linkInfo.hostServiceUsed) {
				is NfsService -> file
				is IscsiService -> "network"
				else -> TODO("not handled service type: $linkInfo")
			}
		} else {
			allocationTypeToDiskType[linkInfo.allocation.javaClass.kotlin] ?: TODO()
		}

fun allocationType(deviceDyn: VirtualStorageLinkInfo): String = deviceDyn.allocation.let {
	when (it) {
		is VirtualStorageLvmAllocation -> "raw"
		is VirtualStorageFsAllocation -> it.type.name.toLowerCase()
		else -> TODO("not handled allocation type: ${it.javaClass.name}")
	}
}

fun XmlBuilder.allocationToXml(linkInfo: VirtualStorageLinkInfo, targetHost: Host) {
	if (isRemoteHost(linkInfo, targetHost)) {
		when (linkInfo.hostServiceUsed) {
			is NfsService -> {
				!"nfs"
				source(file to "/mnt/${linkInfo.allocation.hostId}/\${linkInfo.allocation.getPath(linkInfo.device.stat.id)")
			}
			is IscsiService -> {
				!iscsi
				source("protocol" to iscsi, name to "${iscsiStorageId(linkInfo.device.stat.id)}/1") {
					"host"(name to linkInfo.storageHost.stat.address, "port" to 3260)
				}
				if (linkInfo.hostServiceUsed.password != null) {
					"auth"("username" to iscsiUser) {
						"secret"(type to iscsi, uuid to linkInfo.device.stat.id)
					}
				} else !"unauthenticated"
			}
			else -> TODO("not handled service type: $linkInfo")
		}
	} else {
		when (val allocation = linkInfo.allocation) {
			is VirtualStorageFsAllocation -> {
				!"local ${allocation.type} file allocation"
				source(file to allocation.fileName)
			}
			is VirtualStorageLvmAllocation -> {
				!"local lvm allocation"
				source(dev to allocation.path)
			}
			else -> TODO("not handled allocation type: ${allocation.javaClass.name}")
		}
	}
}

fun isRemoteHost(linkInfo: VirtualStorageLinkInfo, targetHost: Host) = linkInfo.allocation.hostId != targetHost.id

fun vmDefinitionToXml(
		vm: VirtualMachine, disks: List<VirtualStorageLinkInfo>, password: String, targetHost: Host
): String = xml(root = "domain", atts = *arrayOf(type to "kvm")) {
	!"vm name: ${vm.name}"
	name { -vm.id }
	uuid { -vm.id }
	"memory"(unit to bytes) { -vm.memory.min }
	"memtune" {
		"hard_limit"(unit to bytes) { -vm.memory.min }
	}
	"memoryBacking" {
		"allocation"("mode" to "ondemand")
	}
	"vcpu" { text(vm.nrOfCpus) }
	"os" {
		type("arch" to "x86_64") { -"hvm" }
		boot(dev to "hd")
		boot(dev to "cdrom")
	}
	"features" {
		"acpi"()
		"apic"()
		"pae"()
		"hap"()
	}
	"devices" {
		"input"(type to "keyboard", "bus" to "ps2")
		"graphics"(type to "spice", "autoport" to "yes", "listen" to allAddresses, "password" to password) {
			"listen"(type to address, address to allAddresses)
			"image"("compression" to "off")
		}
		"video" {
			"model"(type to "qxl", "ram" to 65535, "vram" to 65535, "vgamem" to 16384, "heads" to 1)
			address(type to "pci", "domain" to "0x0000")
		}
		var targetDev = 'a'
		for (link in disks) {
			"disk"(type to kvmDeviceType(link, targetHost), "device" to link.link.device.name.toLowerCase()) {
				"driver"(name to "qemu", type to allocationType(link), "cache" to "none")
				if (link.device.stat.readOnly || link.link.readOnly) {
					"readonly"()
				}
				this.allocationToXml(link, targetHost)
				"target"(dev to "sd$targetDev", "bus" to link.link.bus)
			}
			targetDev++
		}
	}
}.use { it.reader().readText() }
