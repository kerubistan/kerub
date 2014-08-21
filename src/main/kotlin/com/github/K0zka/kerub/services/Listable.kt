package com.github.K0zka.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.Api

public trait Listable<T> {
	ApiOperation("List all objects", notes = "The actual list you get will be filtered by security")
	GET
	Path("/")
	fun listAll() : List<T>
}