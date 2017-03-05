package com.github.K0zka.kerub

import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey

fun getTestKey() : KeyPair {
	val keyStore = KeyStore.getInstance("JKS")
	Thread.currentThread().contextClassLoader.getResourceAsStream("testkeystore.jks").use {
		keyStore.load(it, "password".toCharArray())
	}
	val key = keyStore.getKey("kerub.testkey", "password".toCharArray())
	val cert = keyStore.getCertificate("kerub.testkey")
	return KeyPair(cert.publicKey, key as PrivateKey)
}