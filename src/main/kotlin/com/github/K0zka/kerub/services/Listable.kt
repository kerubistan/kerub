package com.github.K0zka.kerub.services

import javax.ws.rs.GET

public trait Listable<T> {
	GET
	fun listAll() : List<T>
}