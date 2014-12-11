package com.github.K0zka.kerub.host

import org.apache.sshd.client.channel.AbstractClientChannel
import org.apache.sshd.ClientSession
import java.io.IOException
import com.github.K0zka.kerub.utils.getLogger
import org.slf4j.Logger
import org.apache.sshd.client.SftpClient
import java.util.EnumSet
import org.apache.sshd.client.SftpClient.OpenMode

private val logger = getLogger(javaClass<ClientSession>())

private fun <T> Logger.debugAndReturn(msg: String, x: T): T {
	this.info("${msg} ${x}")
	return x
}



public fun <T> AbstractClientChannel.use(fn: (AbstractClientChannel) -> T): T {
	try {
		this.open().await()
		return fn(this)
	} finally {
		logger.debug("closing client channel")
		this.close(true)
	}
}

public fun ClientSession.execute(command: String): String {
	return this.createExecChannel(command).use {
		it.getInvertedOut().reader("ASCII").use {
			logger.debugAndReturn("result of command ${command}: ", it.readText())
		}
	}
}

/**
 * Check if a file exists.
 * Sftp channel is created, only use this if there is no sftp channel open yet.
 */
public fun ClientSession.checkFileExists(file: String): Boolean {
	return this.createSftpClient().use {
		it.checkFileExists(file)
	}
}

/**
 * Check if a file exists.
 */
public fun SftpClient.checkFileExists(file: String): Boolean {
	try {
		this.stat(file)
		return true
	} catch (e: IOException) {
		//this is fine, it happens when the file does not exist
		return false
	}
}

public fun ClientSession.appendToFile(file : String, content:String) {
	this.createSftpClient().use {
		it.appendToFile(file, content)
	}
}

public fun SftpClient.appendToFile(file : String, content:String) {
	val handle = this.open(file, EnumSet.of<OpenMode>(OpenMode.Append, OpenMode.Create, OpenMode.Write))
	try {
		val contentBytes = content.toByteArray("ASCII")
		val stat = this.stat(handle)
		this.write(handle, stat.size, contentBytes, 0, contentBytes.size())
	} finally {
		this.close(handle)
	}
}

/**
 * Open an sftp channel and perform actions on it
 */
fun <T> SftpClient.use(action: (client: SftpClient) -> T): T {
	try {
		return action(this)
	} finally {
		logger.debug("Closing sftp client")
		try {
			this.close()
		} catch (e: IOException) {
			logger.warn("Could not close sftpclient {} ", this, e)
		}
	}
}

/**
 * Get the contents of a file.
 * This should be used only on small files, usually configuration files.
 * Opens a session and closes it afterwards.
 */
public fun ClientSession.getFileContents(file: String): String {
	return this.createSftpClient().use {
		it.getFileContents(file)
	}
}

/**
 * Get the contents of a file.
 * This should be used only on small files, usually configuration files.
 */
public fun SftpClient.getFileContents(file: String): String {
	return this.read(file).use {
		logger.debugAndReturn("Contents of file ${file}: ", it.reader("ASCII").readText())
	}
}