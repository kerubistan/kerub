package com.github.kerubistan.kerub.host.servicemanager.rc

import com.github.kerubistan.kerub.host.ServiceManager
import com.github.kerubistan.kerub.host.appendToFile
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.iscsi.ctld.Ctld
import com.github.kerubistan.kerub.utils.junix.nfs.Exports
import com.github.kerubistan.kerub.utils.remove
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient

class RcServiceManager(private val session: ClientSession) : ServiceManager {

	companion object {
		private const val configFile = "/etc/rc.conf"
		private val logger = getLogger(RcServiceManager::class)
		private val serviceMap = mapOf<OsCommand, String>(
				Ctld to "ctld",
				Exports to "nfs"
		)

		internal fun isEnabled(serviceName: String, config: String): Boolean =
				config.contains(regex(serviceName))

		private fun regex(serviceName: String) = """"\s*${serviceName}_enable="YES"\s*""".toRegex()

		internal fun readConfig(sftp: SftpClient): String {
			return sftp.read(configFile).reader(Charsets.US_ASCII).use {
				it.readText()
			}
		}

		internal fun disable(serviceName: String, config: String): String =
				config.remove(regex(serviceName))

		internal fun serviceName(service: OsCommand) = requireNotNull(serviceMap[service]) { "service $service not registered" }

	}


	override fun start(service: OsCommand) {
		session.executeOrDie("service start ${serviceName(service)}")
	}

	override fun stop(service: OsCommand) {
		session.executeOrDie("service stop ${serviceName(service)}")
	}

	override fun enable(service: OsCommand) {
		val serviceName = requireNotNull(serviceMap[service])
		session.createSftpClient().use {
			sftp ->
			val config = readConfig(sftp)
			if (isEnabled(serviceName, config)) {
				logger.info("service {} is already enabled in host, skipping")
			} else {
				sftp.appendToFile(
						file = configFile,
						content = """${serviceName}_enable="YES""""
				)
			}
		}
	}

	override fun disable(service: OsCommand) {
		val serviceName = requireNotNull(serviceMap[service])
		session.createSftpClient().use {
			sftp ->
			val config = readConfig(sftp)
			if (isEnabled(serviceName, config)) {
				logger.info("service {} is already enabled in host, skipping")
			} else {
				sftp.write(configFile).writer(Charsets.US_ASCII).use { it.write(disable(serviceName, config)) }
			}
		}
	}
}