package com.github.kerubistan.kerub.exceptions.mappers

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper

class JsonParseExceptionMapper(mapper: ObjectMapper) : AbstractExceptionMapper<JsonParseException>(mapper) {

	companion object {
		val error = RestError("MAP2", "Json parsing error")
	}

	override fun getRestError(exception: JsonParseException) = error
}