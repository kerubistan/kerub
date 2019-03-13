package com.github.kerubistan.kerub.host

import org.apache.sshd.client.session.ClientSession
import java.security.PublicKey

/**
 * Service to help creating ssh client sessions on hosts
 */
interface SshClientService {
	/**
	 * Create an unauthenticated session
	 */
	fun createSession(address: String, userName: String): ClientSession

	/**
	 * Create an authenticated session with public key authentication
	 */
	fun loginWithPublicKey(address: String, userName: String = "root", hostPublicKey: String): ClientSession

	/**
	 * Create an authenticated session with
	 */
	fun loginWithPassword(address: String, userName: String, password: String, hostPublicKey: String): ClientSession

	/**
	 * Install public key on a host.
	 */
	fun installPublicKey(session: ClientSession)

	/**
	 * Get the OpenSSH format of public key.
	 */
	fun getPublicKey(): String

	fun getHostPublicKey(addr : String) : PublicKey
}