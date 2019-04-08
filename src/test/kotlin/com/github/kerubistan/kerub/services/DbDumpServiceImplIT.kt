package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.runRestAction
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream
import kotlin.test.assertTrue

class DbDumpServiceImplIT {
	@Test
	fun checkAuthorizations() {
		assertThrows<RestException> ("unauthenticated user does not have access") {
			val client = createClient()
			client.runRestAction(DbDumpService::class) {
				it.dump()
			}
		}
		assertThrows<RestException> ("normal (not admin) user does not have access") {
			val client = createClient()
			client.login("enduser", "password")
			client.runRestAction(DbDumpService::class) {
				it.dump()
			}
		}
		assertTrue ("Admin should do the trick") {
			val client = createClient()
			client.login("admin", "password")
			client.runRestAction(DbDumpService::class) {
				val dump = it.dump()
				dump.status == 200 && isTarFormat(dump.entity as InputStream)

			}
		}
	}

	private fun isTarFormat(inputStream: InputStream): Boolean {
		TarArchiveInputStream(inputStream).use {
			tar ->
			var entry = tar.nextTarEntry
			while(entry != null) {
				entry = tar.nextTarEntry
			}
		}
		return true
	}
}