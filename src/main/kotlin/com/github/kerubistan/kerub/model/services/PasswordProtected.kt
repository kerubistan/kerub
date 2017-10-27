package com.github.kerubistan.kerub.model.services

interface PasswordProtected : HostService {
	val password: String?
}