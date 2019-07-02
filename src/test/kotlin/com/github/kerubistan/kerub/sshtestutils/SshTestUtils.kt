package com.github.kerubistan.kerub.sshtestutils

import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

fun ClientSession.mockCommandExecution(
		commandMatcher: String = ".*",
		output: String = "",
		error: String = "") : ChannelExec =
		this.mockCommandExecution(commandMatcher.toRegex(), output, error)

fun ClientSession.mockCommandExecution(
		commandMatcher: Regex,
		output: String = "",
		error: String = "") : ChannelExec =
		mock<ChannelExec>().let {exec ->
			whenever(this.createExecChannel(argThat { matches(commandMatcher) })).thenReturn(exec)
			whenever(exec.open()).thenReturn(mock())
			whenever(exec.invertedErr).then { error.toInputStream() }
			whenever(exec.invertedOut).then { output.toInputStream() }
			exec
		}

fun ClientSession.mockCommandExecution(
		commandMatcher: Regex,
		outputs : List<String> = listOf()
		) : ChannelExec =
		mock<ChannelExec>().let {exec ->
			val iterator = outputs.iterator()
			whenever(this.createExecChannel(argThat { matches(commandMatcher) })).thenReturn(exec)
			whenever(exec.open()).thenReturn(mock())
			whenever(exec.invertedOut).then {
				iterator.next().toInputStream()
			}
			whenever(exec.invertedErr).then { NullInputStream(0) }
			exec
		}


fun ClientSession.verifyCommandExecution(commandMatcher: Regex) {
	verify(this).createExecChannel(argThat { commandMatcher.matches(this) })
}

fun ClientSession.verifyCommandExecution(commandMatcher: Regex, mode: org.mockito.verification.VerificationMode) {
	verify(this, mode).createExecChannel(argThat { commandMatcher.matches(this) })
}


fun ClientSession.mockProcess(commandMatcher: Regex, output: String, stderr : String = "") {
	val execChannel: ChannelExec = mock()
	val openFuture : OpenFuture = mock()
	whenever(this.createExecChannel(argThat { commandMatcher.matches(this) })).thenReturn(execChannel)
	whenever(execChannel.open()).thenReturn(openFuture)
	doAnswer {
		val out = it.arguments[0] as OutputStream
		output.forEach { chr ->
			out.write( chr.toInt() )
		}
		null
	} .whenever(execChannel)!!.out = any()
	doAnswer {
		val err = it.arguments[0] as OutputStream
		stderr.forEach { chr ->
			err.write( chr.toInt() )
		}
		null
	} .whenever(execChannel)!!.err = any()

	whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))

}