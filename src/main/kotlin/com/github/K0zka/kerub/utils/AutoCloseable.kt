package com.github.K0zka.kerub.utils

/**
 * This is needed because of the lack of java 1.7 + java 1.8 support in kotlin extension functions
 */
fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
	try {
		return block(this)
	} finally {
		this.close()
	}
}

