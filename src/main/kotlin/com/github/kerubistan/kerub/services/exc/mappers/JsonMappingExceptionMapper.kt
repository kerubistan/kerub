package com.github.kerubistan.kerub.services.exc.mappers

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper

class JsonMappingExceptionMapper(mapper: ObjectMapper) : AbstractExceptionMapper<JsonMappingException>(mapper) {
	companion object {
		val error = RestError("MAP1", "Mapping error.")
	}

	override fun getRestError(exception: JsonMappingException) = error
}