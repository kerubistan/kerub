package com.github.K0zka.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.Api
import javax.ws.rs.PathParam
import javax.ws.rs.DefaultValue
import com.wordnik.swagger.annotations.ApiParam
import javax.ws.rs.QueryParam

public trait Listable<T> {
	ApiOperation("List all objects", notes = "The actual list you get will be filtered by security")
	GET
	Path("/")
	fun listAll(ApiParam("First returned entity", defaultValue = "0", required = false)
	            QueryParam("start")
	            DefaultValue("0") start : Long,
	            ApiParam("Maximum number of returned entities", defaultValue = "20", required = false)
	            QueryParam("limit")
	            DefaultValue("20") limit : Long,
	            ApiParam("Property name to sort by", defaultValue = "id", required = false)
	            QueryParam("sort")
	            DefaultValue("id") sort : String
	            ) : ResultPage<T>
}