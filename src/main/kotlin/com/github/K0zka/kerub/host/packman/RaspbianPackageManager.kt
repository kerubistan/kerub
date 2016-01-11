package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.dpkg.Dpkg
import org.apache.sshd.ClientSession

public class RaspbianPackageManager(session : ClientSession) : AptPackageManager(session){
	override fun list(): List<SoftwarePackage> = Dpkg.listRasbianPackages(session)
}