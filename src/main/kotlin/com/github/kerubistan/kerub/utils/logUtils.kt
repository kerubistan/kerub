package com.github.kerubistan.kerub.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun getLogger(cl: KClass<*>): Logger {
	return LoggerFactory.getLogger(cl.java)!!
}

fun getLogger(logger: String): Logger {
	return LoggerFactory.getLogger(logger)!!
}