package com.github.kerubistan.kerub.services

import java.net.ServerSocket

fun getFreePort(): Int =
		ServerSocket(0).use {
			return it.localPort
		}


fun getBaseUrl(): String {
	return System.getProperty("kerub.it.url") ?: "http://localhost:${getPort()}/"
}

fun getPort(): Int {
	val portStr = System.getProperty("kerub.it.port")
	return portStr?.toInt() ?: 8080
}

fun getServiceBaseUrl(): String {
	return "${getBaseUrl()}/s/r"
}