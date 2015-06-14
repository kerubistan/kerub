package com.github.K0zka.kerub.host

import org.apache.sshd.ClientSession
import com.github.K0zka.kerub.host.use
import com.github.K0zka.kerub.host.checkFileExists
import com.github.K0zka.kerub.host.getFileContents

/**
 * Service to help creating ssh client sessions on hosts
 */
public interface SshClientService {
	/**
	 * Create an unauthenticated session
	 */
	fun createSession(address: String, userName : String) : ClientSession

	/**
	 * Create an authenticated session with public key authentication
	 */
	fun loginWithPublicKey(address: String, userName : String = "root") : ClientSession

	/**
	 * Create an authenticated session with
	 */
	fun loginWithPassword(address: String, userName : String, password : String) : ClientSession

	fun installPublicKey(session: ClientSession)
}