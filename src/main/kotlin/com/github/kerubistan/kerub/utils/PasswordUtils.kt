package com.github.kerubistan.kerub.utils

import java.nio.charset.Charset
import java.util.Base64

// move to kroki
fun ByteArray.base64decode(charset: Charset = Charsets.UTF_8) = Base64.getDecoder().decode(this).toString(charset)
