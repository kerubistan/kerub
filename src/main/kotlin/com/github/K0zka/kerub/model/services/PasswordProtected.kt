package com.github.K0zka.kerub.model.services

interface PasswordProtected : HostService {
	val password : String?
}