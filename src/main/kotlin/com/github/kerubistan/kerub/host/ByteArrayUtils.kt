package com.github.kerubistan.kerub.host

// move to kroki
fun ByteArray.toBase64(): String = java.util.Base64.getEncoder().encodeToString(this)
