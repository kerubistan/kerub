package com.github.K0zka.kerub.utils.junix.qemu

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.utils.createObjectMapper
import org.apache.sshd.ClientSession
import java.math.BigInteger
import kotlin.reflect.jvm.java

/**
 * Utility wrapper around qemu-img
 * See http://manpages.ubuntu.com/manpages/utopic/man1/qemu-img.1.html
 */
public object QemuImg {
	val objectMapper = createObjectMapper()
	fun create(
			session: ClientSession,
			format: VirtualDiskFormat = VirtualDiskFormat.raw,
			size: BigInteger,
			path: String
	          ) {
		session.execute("qemu-img create -f ${format} ${path} ${size}")
	}

	fun info(session: ClientSession, path: String): ImageInfo {
		val ret = session.execute("qemu-img info ${path}")
		return objectMapper.readValue(ret, ImageInfo::class.java)
	}

	fun resize(session: ClientSession, path: String, size: BigInteger) {
		session.execute("qemu-img resize ${path} ${size}")
	}

	fun convert(session: ClientSession, path: String, targetPath : String, targetFormat: VirtualDiskFormat) {
		session.execute("qemu-img convert -O ${targetFormat} ${path} ${targetPath}")
	}
}