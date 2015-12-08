package com.github.K0zka.kerub.utils

val logger = getLogger("com.github.K0zka.kerub.utils")

fun <T> silent( body : () -> T) : T? = silent(body, "")

fun <T> silent( body : () -> T, actionName : String) : T? {
	try {
		return body()
	} catch(exc : Exception) {
		logger.warn("Exception occured during execution: $actionName", exc)
		return null
	}
}