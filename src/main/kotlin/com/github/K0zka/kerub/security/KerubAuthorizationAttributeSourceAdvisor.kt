package com.github.K0zka.kerub.security

import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresGuest
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.authz.annotation.RequiresRoles
import org.apache.shiro.authz.annotation.RequiresUser
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method

class KerubAuthorizationAttributeSourceAdvisor : AuthorizationAttributeSourceAdvisor() {

	companion object {
		private val securityAnnotations = arrayOf(
				RequiresPermissions::class.java, RequiresRoles::class.java,
				RequiresUser::class.java, RequiresGuest::class.java, RequiresAuthentication::class.java
		)
	}

	override fun matches(method: Method, targetClass: Class<*>): Boolean
			=
			super.matches(method, targetClass)
					|| securityAnnotations.any { AnnotationUtils.findAnnotation(targetClass, it) != null }

}