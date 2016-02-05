package com.github.K0zka.kerub.utils

private val logger = getLogger("com.github.K0zka.kerub.utils")

fun <T> silent(body: () -> T): T? = silent(body, "")

fun <T> silent(body: () -> T, actionName: String): T? {
	try {
		return body()
	} catch(exc: Exception) {
		logger.warn("Exception occured during execution: $actionName", exc)
		return null
	}
}

fun <T> silent(actionName: String, body: () -> T): T? {
	try {
		return body()
	} catch(exc: Exception) {
		logger.warn("Exception occured during execution: $actionName", exc)
		return null
	}
}