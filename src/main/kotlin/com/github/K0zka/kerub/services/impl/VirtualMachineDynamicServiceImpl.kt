package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.services.VirtualMachineDynamicService
import java.util.UUID

class VirtualMachineDynamicServiceImpl(
		dao: VirtualMachineDynamicDao,
		private val vmDao : VirtualMachineDao
) : AbstractDynamicServiceImpl<VirtualMachineDynamic>(dao, "vm-dynamic"),
		VirtualMachineDynamicService {
	override fun spiceConnection(id: UUID): String {
		val vmDyn = getById(id)
		require(vmDyn.status == VirtualMachineStatus.Up)
		val vm = assertExist("vm", vmDao[id], id)
		val displaySettings = assertExist("vm display", vmDyn.displaySetting, vmDyn.id)

		return """
[virt-viewer]
type=spice
host=${displaySettings.hostAddr}
port=${displaySettings.port}
password=${displaySettings.password}
delete-this-file=1
fullscreen=0
title=${vm.name} - Release cursor: SHIFT+F12
toggle-fullscreen=shift+f11
release-cursor=shift+f12
secure-attention=ctrl+alt+end
tls-port=${displaySettings.port}
enable-smartcard=0
enable-usb-autoshare=1
tls-ciphers=DEFAULT
host-subject=O=engine,CN=${displaySettings.hostAddr}
ca=${displaySettings.ca}
secure-channels=main;inputs;cursor;playback;record;display;smartcard;usbredir

"""

	}


}