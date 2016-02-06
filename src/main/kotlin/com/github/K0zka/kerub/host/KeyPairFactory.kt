package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.utils.emptyString
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey

class KeyPairFactory {
	var keyStorePath: String = "keystore.jks"
	var keyStorePassword = emptyString
	var certificatePassword = emptyString
	var alias = "kerub"

	fun createKeyPair(): KeyPair {
		val keyStore = KeyStore.getInstance("JKS")
		Thread.currentThread().contextClassLoader.getResourceAsStream(keyStorePath).use {
			if (it == null) {
				throw IllegalArgumentException("Keystore ${keyStorePath} not found")
			}
			keyStore.load(it, keyStorePassword.toCharArray())
		}
		val key = keyStore.getKey(alias, certificatePassword.toCharArray());
		val cert = keyStore.getCertificate(alias)
		return KeyPair(cert.publicKey, key as PrivateKey)
	}
}