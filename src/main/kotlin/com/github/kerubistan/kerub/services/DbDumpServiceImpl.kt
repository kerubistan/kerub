package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.utils.createObjectMapper
import io.github.kerubistan.kroki.exceptions.getStackTraceAsString
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.infinispan.manager.EmbeddedCacheManager
import javax.ws.rs.core.Response
import javax.ws.rs.core.StreamingOutput

class DbDumpServiceImpl(private val cacheManager: EmbeddedCacheManager) : DbDumpService {
	companion object {
		private val mapper = createObjectMapper(true)
	}

	override fun dump(): Response =
			Response.ok().header("content-disposition", """attachment; filename="dump.tar"""")
					.entity(StreamingOutput { output ->
						TarArchiveOutputStream(output).use { tar ->
							cacheManager.cacheNames.map { name -> name to cacheManager.getCache<Any, Any>(name) }
									.forEach { (cacheName, cache) ->
										tar.putArchiveEntry(TarArchiveEntry("$cacheName/"))
										tar.closeArchiveEntry()
										cache.advancedCache.keys.forEach { key ->
											val value = cache.advancedCache[key]
											try {

												val serialized =
														mapper.writeValueAsString(value).toByteArray()
												tar.putArchiveEntry(TarArchiveEntry("$cacheName/$key.json").apply {
													size =
															serialized.size.toLong()
												})
												tar.write(serialized)
												tar.closeArchiveEntry()
											} catch (t: Throwable) {
												val stack =
														"value $value \n for key $key \ncould not be serialized\n${t.getStackTraceAsString()}\n".toByteArray()
												tar.putArchiveEntry(TarArchiveEntry("$cacheName/$key-error.txt").apply {
													size =
															stack.size.toLong()
												})
												tar.write(stack)
												tar.closeArchiveEntry()
											}
										}
									}
						}
					}).build()

}