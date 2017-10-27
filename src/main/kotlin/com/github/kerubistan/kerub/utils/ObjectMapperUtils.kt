package com.github.kerubistan.kerub.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun createObjectMapper(prettyPrint: Boolean = true): ObjectMapper {
	return ObjectMapper().registerModule(KotlinModule())
			.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint)
}
