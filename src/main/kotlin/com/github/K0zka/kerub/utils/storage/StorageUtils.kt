package com.github.K0zka.kerub.utils.storage

import java.util.UUID

private val iscsiVolumePrefix: String = "iqn.io.github.kerub"

fun iscsiStorageId(id: UUID) =
		// iqn.a.b.c:ID format required by iscsi standard - http://tools.ietf.org/html/rfc3720#section-12.4
		"${iscsiVolumePrefix}:${id}"
