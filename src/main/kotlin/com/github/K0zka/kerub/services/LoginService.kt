package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.security.Roles
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface LoginService {
	data class UserData(
			val name : String,
			val roles : List<Roles>
	                   )

	data class UsernamePassword(
			val username: String,
			val password: String
	                           )

    @Path("login")
    @POST
	fun login(authentication: UsernamePassword)

    @GET
    @Path("user")
	fun getUser() : UserData

    @Path("logout")
    @POST
	fun logout()
}