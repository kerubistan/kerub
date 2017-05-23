package com.github.K0zka.kerub.utils.countdown

import java.util.concurrent.TimeoutException

open class Timer(val start: Long = System.currentTimeMillis(), val limit: Long) {
	open fun now() = System.currentTimeMillis()
	fun check(): Int =
			(start + limit - now()).let {
				if (it <= 0) {
					throw TimeoutException()
				} else {
					it.toInt()
				}

			}
}