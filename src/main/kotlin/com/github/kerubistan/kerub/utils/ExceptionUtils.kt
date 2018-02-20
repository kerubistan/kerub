package com.github.kerubistan.kerub.utils

import java.io.PrintWriter
import java.io.StringWriter

private val logger = getLogger("com.github.kerubistan.kerub.utils")

fun Throwable.getStackTraceAsString(): String =
		StringWriter().use {
			PrintWriter(it).use {
				this.printStackTrace(it)
			}
			it.toString()
		}

fun <T> silent(actionName: String = "", body: () -> T): T? {
	return try {
		body()
	} catch(exc: Exception) {
		logger.warn("Exception occured during execution: $actionName", exc)
		null
	}
}

fun <T> insist(tries: Int, action: () -> T): T {
	for (attempt in 0 until tries) {
		try {
			return action()
		} catch (e: Exception) {
			logger.warn("Attempt $attempt failed", e)
		}
	}
	return action()
}