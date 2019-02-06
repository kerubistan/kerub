package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.host.HostCommandExecutor
import org.apache.sshd.client.session.ClientSession

abstract class AbstractNfsShareExecutor<T : AbstractNfsShareStep> : AbstractNfsExecutor<T>() {
	abstract val hostExecutor: HostCommandExecutor

	final override fun perform(step: T) =
			hostExecutor.execute(host = step.host, closure = { performOnHost(it, step) })

	abstract fun performOnHost(session: ClientSession, step: T)

}