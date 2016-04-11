package com.github.K0zka.kerub.host

import java.io.File
import java.nio.file.FileStore
import java.nio.file.FileSystem
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.WatchService
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider

class HostFileSystem (val map : Map<String, File>) : FileSystem() {

	override fun getSeparator(): String {
		throw UnsupportedOperationException()
	}

	override fun newWatchService(): WatchService? {
		throw UnsupportedOperationException()
	}

	override fun supportedFileAttributeViews(): MutableSet<String>? {
		throw UnsupportedOperationException()
	}

	override fun isReadOnly(): Boolean = true

	override fun getFileStores(): Iterable<FileStore> = listOf()

	override fun getPath(p0: String?, vararg p1: String?): Path? {
		throw UnsupportedOperationException()
	}

	override fun provider(): FileSystemProvider? {
		throw UnsupportedOperationException()
	}

	override fun isOpen(): Boolean = true

	override fun getUserPrincipalLookupService(): UserPrincipalLookupService? {
		throw UnsupportedOperationException()
	}

	override fun close() {
	}

	override fun getPathMatcher(p0: String?): PathMatcher? {
		throw UnsupportedOperationException()
	}

	override fun getRootDirectories(): MutableIterable<Path>? {
		throw UnsupportedOperationException()
	}

	//	override fun getFile(file: String?): SshFile? {
//		return map[file]
//	}
//
//	override fun getFile(baseDir: SshFile?, file: String?): SshFile? {
//		return getFile("${baseDir?.absolutePath}/${file}")
//	}
//
//	override fun getNormalizedView(): FileSystemView? {
//		return this
//	}

}
