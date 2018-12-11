package com.github.kerubistan.kerub.utils.junix.sysctl

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.process
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

object BsdSysCtl : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?): Boolean = hostCapabilities?.os == OperatingSystem.BSD

	private val splitter = Regex.fromLiteral("\$kern\\.")
	private const val separator = "---end---"

	fun getCpuFlags(session: ClientSession): List<String> {

		val output = session.executeOrDie("sysctl -a")
		val props = output.split(splitter)


		return listOf()
	}

	fun parseTemperatures(output: String) = output.lines()
			.filter(String::isNotBlank).map {
				it.substringBetween("dev.cpu.", ".temperature").toInt() to it.substringBetween(
						"temperature:",
						"C").trim().toInt()
			}

	fun getCpuTemperatures(session: ClientSession) =
			parseTemperatures(session.executeOrDie("""bash -c "sysctl dev.cpu | grep temperature" """))

	private class CpuTemperaturesOutputStream(private val handler: (List<Pair<Int, Int>>) -> Unit) : OutputStream() {
		private val buffer = StringBuilder()
		override fun write(data: Int) {
			buffer.append(data.toChar())
			if (buffer.endsWith(separator)) {
				handler(parseTemperatures(buffer.toString().substringBefore(separator)))
				buffer.clear()
			}
		}
	}

	fun monitorCpuTemperatures(session: ClientSession, interval: Int = 1, handler: (List<Pair<Int, Int>>) -> Unit) =
			session.process(
					"""bash -c "while true; do sysctl dev.cpu | grep temperature" """,
					CpuTemperaturesOutputStream(handler))

}