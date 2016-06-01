package com.github.K0zka.kerub.utils.storage

import java.util.UUID

val iscsiVolumePrefix: String = "iqn.io.github.kerub"

fun iscsiStorageId(id: UUID) =
		"${iscsiVolumePrefix}.${id}"
