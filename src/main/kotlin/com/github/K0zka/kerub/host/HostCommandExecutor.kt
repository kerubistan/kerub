package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.model.Host
import org.apache.sshd.client.session.ClientSession

interface HostCommandExecutor {
	/**
	 * Execute a command on a connected host
	 */
	fun <T> execute(host: Host, closure: (ClientSession) -> T) : T

	/**
	 * Perform an action on the session, if the host is not connected by a control connection,
	 * create a temporary data connection
	 */
	fun <T> dataConnection(host: Host, action: (session: ClientSession) -> T): T

}