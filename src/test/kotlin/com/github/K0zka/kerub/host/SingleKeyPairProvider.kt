package com.github.K0zka.kerub.host

import org.apache.sshd.common.KeyPairProvider
import java.security.KeyPair

class SingleKeyPairProvider (val keyPair : KeyPair) : KeyPairProvider {
	override fun loadKeys(): Iterable<KeyPair> {
		return listOf( keyPair )
	}

	override fun loadKey(type: String?): KeyPair? {
		return keyPair
	}

	override fun getKeyTypes(): String? {
		return "ssh-${keyPair.public.algorithm.toLowerCase()}"
	}
}