package com.github.kerubistan.kerub.sshtestutils

import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession

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
			whenever(exec.open()).thenReturn(mock<OpenFuture>())
			whenever(exec.invertedErr).then { error.toInputStream() }
			whenever(exec.invertedOut).then { output.toInputStream() }
			exec
		}