package com.github.K0zka.kerub.host

import org.apache.sshd.client.channel.AbstractClientChannel
import org.apache.sshd.ClientSession
import java.io.IOException
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.client.SftpClient.OpenMode
import java.util.EnumSet
import org.slf4j.Logger

private val logger = getLogger(javaClass<ClientSession>())

private fun <T> Logger.debugAndReturn(msg: String, x : T) : T {
	this.info("${msg} ${x}")
	return x
}

public fun <T> AbstractClientChannel.use(fn: (AbstractClientChannel) -> T): T {
	try {
		this.open().await()
		return fn(this)
	} finally {
		logger.debug("closing client channel")
		this.close(false)
	}
}

public fun ClientSession.execute(command: String): String {
	return this.createExecChannel(command).use {
		it.getInvertedOut().reader("ASCII").use {
			logger.debugAndReturn("result of command ${command}: ",it.readText())
		}
	}
}

public fun ClientSession.checkFileExists(file: String): Boolean {
	val client = this.createSftpClient()
	try {
		client.stat(file)
		return true
	} catch (e: IOException) {
		return false
	} finally {
		client.close()
	}
}

/**
 * Get the contents of a file.
 * This should be used only on small files, usually configuration files.
 */
public fun ClientSession.getFileContents(file : String) : String {
	val client = this.createSftpClient()
	try {
		return client.read(file).use {
			logger.debugAndReturn("Contents of file ${file}: ", it.reader("ASCII").readText())
		}
	} finally {
		client.close()
	}
}