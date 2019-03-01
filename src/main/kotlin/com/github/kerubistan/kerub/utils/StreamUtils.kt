package com.github.kerubistan.kerub.utils

import java.nio.charset.Charset

fun resource(resource: String) =
		requireNotNull(Thread.currentThread()
				.contextClassLoader
				.getResource(resource)) { "$resource not found" }
				.openStream()!!

fun resourceToString(resource: String, charset: Charset = Charsets.UTF_8) =
		resource(resource)
				.use { it.reader(charset).readText() }