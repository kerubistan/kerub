package com.github.K0zka.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.DELETE
import javax.ws.rs.PUT

public trait RestCrud<T, I> {
	GET
	Path("/{id}")
	fun getById(PathParam("id") id : I) : T

	POST
	Path("/{id}")
	fun update(PathParam("id") id : I, entity: T) : T

	DELETE
	Path("/{id}")
	fun delete(PathParam("id") id : I)

	PUT
	Path("/")
	fun add(entity: T) : T
}