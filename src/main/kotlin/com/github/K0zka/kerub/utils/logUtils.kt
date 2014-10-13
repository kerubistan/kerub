package com.github.K0zka.kerub.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun getLogger(cl : Class<*>) : Logger {
	return LoggerFactory.getLogger(cl)!!
}