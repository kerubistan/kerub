package com.github.kerubistan.kerub.utils.junix.benchmarks.fio

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession
import java.util.UUID

object Fio : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?): Boolean
			= hostCapabilities?.installedSoftware?.any { it.name == "fio" } ?: false

	private val runtimeLimit = 10
	private val mapper = createObjectMapper(prettyPrint = false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	fun benchmarkIoDevice(session: ClientSession, device: String): FioBenchmarkResults {
		session.createSftpClient().use {
			sftp ->
			val iniFile = "/tmp/${UUID.randomUUID()}"
			sftp.write(iniFile).writer(Charsets.US_ASCII).use {
				it.write("""
[rand]
filename=$device
readwrite=randrw
runtime=$runtimeLimit

[seq]
filename=$device
readwrite=rw
runtime=$runtimeLimit
				""")
			}

			return mapper.readValue(session.executeOrDie("fio $iniFile --output-format=json"))
		}
	}
}