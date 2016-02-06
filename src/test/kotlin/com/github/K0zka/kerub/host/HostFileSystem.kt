package com.github.K0zka.kerub.host

import org.apache.sshd.common.file.FileSystemView
import org.apache.sshd.common.file.SshFile

class HostFileSystem (val map : Map<String, SshFile>) : FileSystemView {
	override fun getFile(file: String?): SshFile? {
		return map[file]
	}

	override fun getFile(baseDir: SshFile?, file: String?): SshFile? {
		return getFile("${baseDir?.absolutePath}/${file}")
	}

	override fun getNormalizedView(): FileSystemView? {
		return this
	}

}
