package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.security.Roles
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

public trait LoginService {
	data class UserData(
			val name : String,
			val roles : List<Roles>
	                   )

	data class UsernamePassword(
			val username: String,
			val password: String
	                           )

	Path("login")
	POST
	fun login(authentication: UsernamePassword)

	GET
	Path("user")
	fun getUser() : UserData

	Path("logout")
	POST
	fun logout()
}