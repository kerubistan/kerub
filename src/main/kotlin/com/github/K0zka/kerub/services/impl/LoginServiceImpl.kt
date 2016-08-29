package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.security.Roles
import com.github.K0zka.kerub.services.LoginService
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.UsernamePasswordToken

class LoginServiceImpl : LoginService {

	override
	fun login(authentication: LoginService.UsernamePassword) {
		val subject = SecurityUtils.getSubject()
		subject.login(UsernamePasswordToken(
				authentication.username,
				authentication.password)
		)
	}

	override
	fun getUser(): LoginService.UserData {
		val subject = SecurityUtils.getSubject()
		return LoginService.UserData(
				subject.principal.toString(),
				Roles.values().filter { subject.hasRole(it.roleName) }.toList()
		)
	}

	override
	fun logout() {
		val subject = SecurityUtils.getSubject()
		subject.logout()
	}
}