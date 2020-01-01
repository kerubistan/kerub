package com.github.kerubistan.kerub.host

// moved to kroki
fun ByteArray.toBase64(): String = java.util.Base64.getEncoder().encodeToString(this)
