package com.github.K0zka.kerub.services

fun getBaseUrl() : String {
	val url = System.getProperty("kerub.it.url")
	if(url == null) {
		return "http://localhost:${getPort()}/"
	} else {
		return url
	}
}

fun getPort() : Int {
	val portStr = System.getProperty("kerub.it.port")
	if(portStr == null) {
		return 8080
	} else {
		return portStr.toInt()
	}
}

fun getServiceBaseUrl() : String {
	return "${getBaseUrl()}/s/r"
}