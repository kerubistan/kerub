package com.github.kerubistan.kerub.model.services

import java.util.UUID

interface StorageService : HostService {
	val vstorageId: UUID
}