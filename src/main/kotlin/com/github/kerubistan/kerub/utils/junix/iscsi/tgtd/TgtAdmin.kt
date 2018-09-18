package com.github.kerubistan.kerub.utils.junix.iscsi.tgtd

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.equalsAnyIgnoreCase
import com.github.kerubistan.kerub.utils.junix.common.Centos
import com.github.kerubistan.kerub.utils.junix.common.Debian
import com.github.kerubistan.kerub.utils.junix.common.Fedora
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.Ubuntu
import com.github.kerubistan.kerub.utils.junix.common.openSuse
import com.github.kerubistan.kerub.utils.storage.iscsiStorageId
import org.apache.sshd.client.session.ClientSession
import java.util.Date
import java.util.UUID
import com.github.kerubistan.kerub.utils.storage.iscsiDefaultUser as user

object TgtAdmin : OsCommand {

	override fun providedBy(): List<Pair<(SoftwarePackage) -> Boolean, List<String>>>
			= listOf(
			{ distro: SoftwarePackage -> distro.name.equalsAnyIgnoreCase(Fedora, Centos) }
					to listOf("scsi-target-utils"),
			{ distro: SoftwarePackage -> distro.name == openSuse } to listOf("tgt"),
			{ distro: SoftwarePackage -> distro.name.startsWith(Debian) } to listOf("tgt"),
			{ distro: SoftwarePackage -> distro.name.startsWith(Ubuntu) } to listOf("tgt")
	)

	fun shareBlockDevice(
			session: ClientSession,
			id: UUID,
			path: String,
			readOnly: Boolean = false,
			password: String? = null) {
		session.createSftpClient().use {
			ftp ->
			ftp.write(configurationPath(id)).use {
				out ->
				//it needs to be backing-store for LVM volumes
				//with direct-store tgtd tries to read scsi info
				out.write("""
#created by kerub on ${Date()}
<target ${iscsiStorageId(id)}>
    backing-store $path
    readonly ${if (readOnly) "1" else "0"}
    ${if (password != null) "incominguser $user $password" else ""}
</target>
			""".toByteArray(charset("ASCII")))
			}
		}
		session.executeOrDie("tgt-admin -e")
	}

	fun unshareBlockDevice(session: ClientSession, id: UUID) {
		//first remove the configuration, if anyone calls tgt-admin -e, this will be automatically removed
		session.createSftpClient().use {
			ftp ->
			ftp.remove(configurationPath(id))
		}
		//remove the configuration from the tgt admin
		session.executeOrDie("tgt-admin --delete ${iscsiStorageId(id)}")
	}

	//TODO: this path may change by OS distribution
	private fun configurationPath(id: UUID) =
			"/etc/tgt/conf.d/$id.conf"

}