package com.github.kerubistan.kerub.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun createObjectMapper(prettyPrint: Boolean = true): ObjectMapper =
		ObjectMapper().registerModule(KotlinModule(strictNullChecks = true))
				.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
				.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true)
				// also the default but I'd like to explicitly state here that
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
				.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
				.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true)
				.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
				.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
				.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint)
