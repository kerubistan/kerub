package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.packman.YumPackageManager
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

class XenServer7 : LsbDistribution("XenServer") {

	override fun handlesVersion(version: Version) = version.major == "7"

	override fun getPackageManager(session: ClientSession) = YumPackageManager(session)

	override fun name() = "XenServer"
}