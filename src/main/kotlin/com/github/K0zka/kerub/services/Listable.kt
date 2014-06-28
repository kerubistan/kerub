package com.github.K0zka.kerub.services

import javax.ws.rs.GET
import javax.ws.rs.Path

public trait Listable<T> {
	GET
	Path("/")
	fun listAll() : List<T>
}