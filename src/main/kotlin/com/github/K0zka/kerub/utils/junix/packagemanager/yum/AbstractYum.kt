package com.github.K0zka.kerub.utils.junix.packagemanager.yum

import com.github.K0zka.kerub.host.execute
import org.apache.sshd.ClientSession

abstract class AbstractYum(private val command : String) {

	fun installPackage(session: ClientSession, pack: String) {
		session.execute("$command -y install ${pack}")
	}

	fun uninstallPackage(session: ClientSession, pack: String) {
		session.execute("$command -y remove ${pack}")
	}


}