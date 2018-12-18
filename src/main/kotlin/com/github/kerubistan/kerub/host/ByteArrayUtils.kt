package com.github.kerubistan.kerub.host

fun ByteArray.toBase64(): String = java.util.Base64.getEncoder().encodeToString(this)
