package com.github.K0zka.kerub.utils.junix.packagemanager.pkg

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.ClientSession

object Pkg {

	fun installPackage(session: ClientSession, pack: String) {
		session.execute("""pkg install -y ${pack} """)
	}

	fun uninstallPackage(session: ClientSession, pack: String) {
		session.execute("""pkg remove -y ${pack} """)
	}

	fun listPackages(session: ClientSession): List<SoftwarePackage>
			= session.execute("""pkg query "%n\t%v" """).split('\n').toList().map {
		val split = it.split('\t')
		SoftwarePackage(split[0], Version.fromVersionString(split[1]))
	}

}