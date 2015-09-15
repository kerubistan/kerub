package com.github.K0zka.kerub.exceptions.mappers

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper

public class JsonParseExceptionMapper(mapper: ObjectMapper) : AbstractExceptionMapper<JsonParseException>(mapper) {

	companion object {
		val error = RestError("MAP2", "Json parsing error")
	}

	override fun getRestError(exception: JsonParseException) = error
}