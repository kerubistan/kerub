package com.github.K0zka.kerub.utils

fun <T : AutoCloseable, R> T.use( block : (T) -> R ) : R {
	try {
		return block(this)
	} finally {
		this.close()
	}
}

