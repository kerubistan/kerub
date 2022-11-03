package com.github.kerubistan.kerub.utils

private val logger = getLogger("com.github.kerubistan.kerub.utils")

enum class LogLevel {
	Off,
	Debug,
	Info,
	Warning,
	Error
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

// moved to kroki
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

fun <T> kick(tries: Int, action: () -> T): T? {
	for (attempt in 0 until tries) {
		try {
			return action()
		} catch (e: Exception) {
			logger.warn("Attempt $attempt failed", e)
		}
	}
	return null
}