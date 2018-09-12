package com.github.kerubistan.kerub.utils.junix.qemu

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.equalsAnyIgnoreCase
import com.github.kerubistan.kerub.utils.equalsAnyOf
import com.github.kerubistan.kerub.utils.junix.common.Centos
import com.github.kerubistan.kerub.utils.junix.common.Debian
import com.github.kerubistan.kerub.utils.junix.common.Fedora
import com.github.kerubistan.kerub.utils.junix.common.FreeBSD
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.Ubuntu
import com.github.kerubistan.kerub.utils.junix.common.openSuse
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

/**
 * Utility wrapper around qemu-img
 * See http://manpages.ubuntu.com/manpages/utopic/man1/qemu-img.1.html
 */
object QemuImg : OsCommand {

	override fun providedBy(): List<Pair<(SoftwarePackage) -> Boolean, List<String>>> {
		return listOf(
				{ os: SoftwarePackage -> os.name.equalsAnyIgnoreCase(Fedora, Centos) }
						to listOf("qemu-img"),
				{ os: SoftwarePackage -> os.name.equalsAnyIgnoreCase(openSuse) }
						to listOf("qemu-tools"),
				{ os: SoftwarePackage -> os.name.equalsAnyIgnoreCase(Ubuntu, Debian, FreeBSD) }
						to listOf("qemu-utils")
		)
	}

	private val objectMapper = createObjectMapper()
	fun create(
			session: ClientSession,
			format: VirtualDiskFormat = VirtualDiskFormat.raw,
			size: BigInteger,
			path: String,
			backingFile : String? = null
	) {
		val options = backingFile?.let { "-o backing_file=$it" } ?: ""
		session.executeOrDie("qemu-img create -f $format $options $path $size")
	}

	fun check(session: ClientSession, path: String, format: VirtualDiskFormat) {
		session.executeOrDie("qemu-img check -f $format $path")
	}

	fun rebase(session: ClientSession,
			   path: String,
			   format: VirtualDiskFormat,
			   backingFile: String) {
		assert(format.equalsAnyOf(VirtualDiskFormat.qed, VirtualDiskFormat.qcow2))
		session.executeOrDie("qemu-img rebase -b $backingFile $path")
	}

	fun info(session: ClientSession, path: String): ImageInfo {
		val ret = session.executeOrDie("qemu-img info --output=json $path")
		return objectMapper.readValue(ret, ImageInfo::class.java)
	}

	fun resize(session: ClientSession, path: String, size: BigInteger) {
		session.executeOrDie("qemu-img resize $path $size")
	}

	fun convert(session: ClientSession, path: String, targetPath: String, targetFormat: VirtualDiskFormat) {
		session.executeOrDie("qemu-img convert -O $targetFormat $path $targetPath")
	}
}