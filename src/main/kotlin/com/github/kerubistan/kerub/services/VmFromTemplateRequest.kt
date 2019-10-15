package com.github.kerubistan.kerub.services

import com.github.kerubistan.kerub.model.AssetOwner
import java.util.UUID

data class VmFromTemplateRequest(
		/**
		 * The name of the new VM, or null of automatic name requested - in this case it will come from the template.
		 */
		val vmName : String? = null,
		val templateId : UUID,
		val owner : AssetOwner? = null
)