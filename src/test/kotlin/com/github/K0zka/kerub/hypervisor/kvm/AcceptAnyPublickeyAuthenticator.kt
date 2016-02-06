package com.github.K0zka.kerub.hypervisor.kvm

import org.apache.sshd.server.PublickeyAuthenticator
import org.apache.sshd.server.session.ServerSession
import java.security.PublicKey

object AcceptAnyPublickeyAuthenticator : PublickeyAuthenticator {
	override fun authenticate(
			username: String,
			key: PublicKey,
			session: ServerSession
	                         ) = true
}