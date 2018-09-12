package com.github.kerubistan.kerub.utils.junix.nfs

import com.github.kerubistan.kerub.host.appendToFile
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.equalsAnyIgnoreCase
import com.github.kerubistan.kerub.utils.equalsAnyOf
import com.github.kerubistan.kerub.utils.junix.common.Centos
import com.github.kerubistan.kerub.utils.junix.common.Fedora
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.Ubuntu
import org.apache.sshd.client.session.ClientSession

object Exports : OsCommand {

	private const val exportsFile = "/etc/exports"

	override fun providedBy(): List<Pair<(SoftwarePackage) -> Boolean, List<String>>> = listOf(
			{ distro: SoftwarePackage -> distro.name.equalsAnyIgnoreCase(Centos, Fedora) } to listOf("nfs-utils"),
			{ distro: SoftwarePackage -> distro.name.equalsAnyIgnoreCase(Ubuntu) } to listOf("nfs-kernel-server")
	)

	fun export(session: ClientSession, directory: String, write : Boolean = false) {
		session.createSftpClient().appendToFile(exportsFile, """
			$directory		*(${if (write) {"rw"} else {"ro"} })
		""".trimIndent())
		refresh(session)
	}

	fun unexport(session: ClientSession, directory: String) {
		session.createSftpClient().use { sftp ->
			val lines = sftp.read(exportsFile).reader(Charsets.US_ASCII).use {
				it.readLines()
			}
			val filtered = lines.filterNot { it.startsWith(directory) }
			sftp.write(exportsFile).bufferedWriter(Charsets.US_ASCII).use {
				writer ->
				filtered.forEach {
					writer.write(it)
					writer.newLine()
				}
			}
		}
		refresh(session)
	}

	fun refresh(session: ClientSession) = session.executeOrDie("exportfs -r")

}