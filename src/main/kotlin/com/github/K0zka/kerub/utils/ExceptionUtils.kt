package com.github.K0zka.kerub.utils

import java.io.PrintWriter
import java.io.StringWriter

private val logger = getLogger("com.github.K0zka.kerub.utils")

fun Throwable.getStackTraceAsString(): String =
		StringWriter().use {
			PrintWriter(it).use {
				this.printStackTrace(it)
			}
			it.toString()
		}

fun <T> silent(actionName: String = "", body: () -> T): T? {
	try {
		return body()
	} catch(exc: Exception) {
		logger.warn("Exception occured during execution: $actionName", exc)
		return null
	}
}

fun <T> insist(tries: Int, action: () -> T): T {
	for (attempt in 0..tries - 1) {
		try {
			return action()
		} catch (e: Exception) {
			logger.warn("Attempt $attempt failed", e)
		}
	}
	return action()
}