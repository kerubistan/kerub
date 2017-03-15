package com.github.K0zka.kerub.security

import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.SecurityManager

class SecurityManagerSetup {
	var securityManager: SecurityManager
		get() = SecurityUtils.getSecurityManager()
		set(value) = SecurityUtils.setSecurityManager(value)
}