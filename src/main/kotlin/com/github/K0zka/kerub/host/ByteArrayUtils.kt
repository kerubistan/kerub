package com.github.K0zka.kerub.host

import org.infinispan.commons.util.Base64

/*
 * Temporary utilities for bytearray manipulation.
 * To be removed as soon as kotlin support is available.
 */

fun ByteArray.toBase64(): String {
	//TODO: also get rid of infinispan dependency here once ported to jdk 1.8
	return Base64.encodeBytes(this, Base64.DONT_BREAK_LINES)
}
