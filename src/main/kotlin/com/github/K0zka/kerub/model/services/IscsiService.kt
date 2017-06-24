package com.github.K0zka.kerub.model.services

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("iscsi")
data class IscsiService(
		override val vstorageId: UUID,
		override val password: String? = null
) : HostService, PasswordProtected, StorageService