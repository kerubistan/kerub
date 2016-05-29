package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.Host
import org.apache.sshd.client.session.ClientSession

// because it is a nightmare to mock function literals
class HostCommandExecutorStub(private val session: ClientSession) : HostCommandExecutor {
	override fun <T> execute(host: Host, closure: (ClientSession) -> T): T {
		return closure(session)
	}

	override fun <T> dataConnection(host: Host, action: (ClientSession) -> T): T {
		return action(session)
	}
}