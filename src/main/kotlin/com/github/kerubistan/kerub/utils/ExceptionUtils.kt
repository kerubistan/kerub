package com.github.kerubistan.kerub.utils

import java.io.PrintWriter
import java.io.StringWriter

private val logger = getLogger("com.github.kerubistan.kerub.utils")

enum class LogLevel {
	Off,
	Debug,
	Info,
	Warning,
	Error
}

fun Throwable.getStackTraceAsString(): String =
		StringWriter().use { stringWriter ->
			PrintWriter(stringWriter).use {
				this.printStackTrace(it)
			}
			stringWriter.toString()
		}

fun <T> silent(level: LogLevel = LogLevel.Info, actionName: String = "", body: () -> T): T? {
	return try {
		body()
	} catch (exc: Exception) {
		val message = "Exception occurred during execution: $actionName"
		when (level) {
			LogLevel.Debug ->
				logger.debug(message, exc)
			LogLevel.Info ->
				logger.info(message, exc)
			LogLevel.Warning ->
				logger.warn(message, exc)
			LogLevel.Error ->
				logger.error(message, exc)
			LogLevel.Off -> {
				/* intentionally nothing */
			}
		}
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