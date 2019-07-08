package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.model.Host
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession

fun HostCommandExecutor.mockHost(host : Host, session: ClientSession) {
	doAnswer {
		(it.arguments[1] as  (ClientSession) -> Any).invoke(session)
	}.whenever(this).execute(eq(host), any<(ClientSession) -> Any>())
}

fun <T> HostCommandExecutor.mockDataConnection(host :Host, clientSession: ClientSession = mock()) {
	doAnswer { mockInvocation ->
		(mockInvocation.arguments[1] as (ClientSession)-> T).invoke(clientSession)
	}.whenever(this).dataConnection(eq(host), action = any<(ClientSession)-> T>())
}