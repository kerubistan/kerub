package com.github.kerubistan.kerub.host

import org.apache.sshd.common.keyprovider.KeyPairProvider
import java.security.KeyPair

class SingleKeyPairProvider (val keyPair : KeyPair) : KeyPairProvider {
	override fun loadKeys(): Iterable<KeyPair> {
		return listOf( keyPair )
	}

	override fun loadKey(type: String?): KeyPair? {
		return keyPair
	}

	override fun getKeyTypes(): Iterable<String> {
		return listOf("ssh-${keyPair.public.algorithm.toLowerCase()}")
	}
}