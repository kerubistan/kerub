package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.services.VirtualMachineService
import java.util.UUID

class VirtualMachineServiceImpl(dao: VirtualMachineDao) : ListableBaseService<VirtualMachine>(dao, "vm"),
		VirtualMachineService {

	override fun spiceConnection(): String {
		return """
[virt-viewer]
type=spice
host=${TODO("https://github.com/kerubistan/kerub/issues/77")}
port=${TODO("https://github.com/kerubistan/kerub/issues/77")}
password=${TODO("https://github.com/kerubistan/kerub/issues/77")}
delete-this-file=1
fullscreen=0
title=${TODO("https://github.com/kerubistan/kerub/issues/77")} - Release cursor: SHIFT+F12
toggle-fullscreen=shift+f11
release-cursor=shift+f12
secure-attention=ctrl+alt+end
tls-port=${TODO("https://github.com/kerubistan/kerub/issues/77")}
enable-smartcard=0
enable-usb-autoshare=1
tls-ciphers=DEFAULT
host-subject=O=engine,CN=192.168.122.71
ca=${TODO("https://github.com/kerubistan/kerub/issues/77")}
secure-channels=main;inputs;cursor;playback;record;display;smartcard;usbredir

"""

	}

	override fun startVm(id: UUID) {
		doWithVm(id, {
			vm ->
			alterAvailabilityExpectations(VirtualMachineAvailabilityExpectation(up = true), vm)
		})
	}

	override fun stopVm(id: UUID) {
		doWithVm(id, {
			vm ->
			alterAvailabilityExpectations(VirtualMachineAvailabilityExpectation(up = false), vm)
		})
	}


	private fun alterAvailabilityExpectations(newExpectation: VirtualMachineAvailabilityExpectation, vm: VirtualMachine): VirtualMachine {
		return vm.copy(
				expectations = vm.expectations
						.filterNot { it is VirtualMachineAvailabilityExpectation }
						+ newExpectation
		)
	}

	internal fun doWithVm(id: UUID, action: (VirtualMachine) -> VirtualMachine) {
		update(id, action(getById(id)))
	}


}