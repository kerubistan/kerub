package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.common.debianFamily
import com.github.kerubistan.kerub.utils.junix.common.openSuse
import com.github.kerubistan.kerub.utils.junix.common.redHatFamily
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object LvmThinLv : Lvm() {
	override fun available(osVersion: SoftwarePackage, packages: List<SoftwarePackage>): Boolean =
			super.available(osVersion, packages)
					// package indirectly used by lvm when allocating thin pools
					&& (osVersion.name in debianFamily && packages.any { it.name == "thin-provisioning-tools" })
					|| (osVersion.name in redHatFamily && packages.any { it.name == "device-mapper-persistent-data" })
					|| (osVersion.name == openSuse
					&& packages.any { it.name == "device-mapper" || it.name == "thin-provisioning-tools" })

	fun createPool(session: ClientSession, vgName: String, name: String, size: BigInteger, metaSize: BigInteger) =
			session.executeOrDie(
					("lvm lvcreate $vgName -n $name -L ${LvmLv.roundUp(size)}B -Wn -Zy -y" +
							" && lvm lvcreate $vgName -n ${name}_meta -L ${LvmLv.roundUp(metaSize)}B -Wn -Zy -y" +
							" && lvm lvconvert --type thin-pool $vgName/$name --poolmetadata ${name}_meta -Zy -y")
							.trimIndent()
					, LvmLv::checkErrorOutput)


}