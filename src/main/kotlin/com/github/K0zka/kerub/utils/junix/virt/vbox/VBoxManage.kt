package com.github.K0zka.kerub.utils.junix.virt.vbox

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.model.io.DeviceType
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object VBoxManage {
	fun startVm(session: ClientSession) {
		TODO()
	}
	fun stopVm(session: ClientSession) {
		TODO()
	}
	fun createMedium(session: ClientSession, path : String, size : BigInteger, type: DeviceType, format: VirtualDiskFormat) {
		session.executeOrDie("VBoxManage create $type --filename $path --format $format")
	}

}

