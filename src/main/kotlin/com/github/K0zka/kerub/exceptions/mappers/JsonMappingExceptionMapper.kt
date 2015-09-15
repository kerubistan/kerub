package com.github.K0zka.kerub.exceptions.mappers

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.exceptions.mappers.RestError
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

public class JsonMappingExceptionMapper(mapper: ObjectMapper) : AbstractExceptionMapper<JsonMappingException>(mapper) {
	companion object {
		val error = RestError("MAP1", "Mapping error.")
	}
	override fun getRestError(exception: JsonMappingException) = error
}