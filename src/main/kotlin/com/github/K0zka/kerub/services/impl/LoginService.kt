package com.github.K0zka.kerub.services.impl

import javax.ws.rs.Path
import javax.ws.rs.POST
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.SecurityUtils
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import com.github.K0zka.kerub.security.Roles

Path("auth")
Produces(MediaType.APPLICATION_JSON)
Consumes(MediaType.APPLICATION_JSON)
public class LoginService {

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
	fun login(authentication: UsernamePassword) {
		val subject = SecurityUtils.getSubject()
		subject.login(UsernamePasswordToken(
				authentication.username,
				authentication.password)
		             )
	}

	GET
	Path("user")
	fun getUser() : UserData {
		val subject = SecurityUtils.getSubject()
		return UserData(
				subject.getPrincipal().toString(),
				Roles.values().filter { subject.hasRole(it.name) } .toList()
		               )
	}

	Path("logout")
	POST
	fun logout() {
		val subject = SecurityUtils.getSubject()
		subject.logout()
	}
}