package com.github.K0zka.kerub.model.services

import java.util.UUID

interface StorageService : HostService {
	val vstorageId: UUID
}