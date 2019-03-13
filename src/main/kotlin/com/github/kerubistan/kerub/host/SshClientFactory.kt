package com.github.kerubistan.kerub.host

import org.apache.sshd.client.SshClient
import org.apache.sshd.common.PropertyResolverUtils


/**
 *
 * See values from ClientFactoryManager for the parameters that make sense in this context.
 */
object SshClientFactory {

	fun build(params: Map<String, Long>): SshClient {
		val client = createSshClient()
		params.forEach {
			PropertyResolverUtils.updateProperty(client, it.key, it.value)
		}
		client.start()
		return client
	}
}