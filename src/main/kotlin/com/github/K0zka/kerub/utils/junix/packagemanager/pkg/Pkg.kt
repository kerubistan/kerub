package com.github.K0zka.kerub.utils.junix.packagemanager.pkg

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.client.session.ClientSession

object Pkg {

	val logger = getLogger(Pkg::class)

	fun installPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg install -y ${packs.joinToString(separator = " ")}""")
	}

	fun uninstallPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("""pkg remove -y ${packs.joinToString(separator = " ")}""")
	}

	fun listPackages(session: ClientSession): List<SoftwarePackage>
			= session.execute("""pkg query "%n\t%v" """).lines().filter { it.isNotEmpty() }.map {
		val split = it.split('\t')
		SoftwarePackage(split[0], Version.fromVersionString(split[1]))
	}

}