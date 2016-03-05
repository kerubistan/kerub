package com.github.K0zka.kerub.utils

import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.io.OutputStream

fun OutputStream.copyFrom(input : InputStream) {
	IOUtils.copy(input, this)
}

fun InputStream.copyTo(output : OutputStream) {
	IOUtils.copy(this, output)
}

