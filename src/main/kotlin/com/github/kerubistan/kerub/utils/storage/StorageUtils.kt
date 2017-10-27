package com.github.kerubistan.kerub.utils.storage

import java.util.UUID

val iscsiDefaultUser = "kerubagent"

private val iscsiVolumePrefix: String = "iqn.io.github.kerub"

fun iscsiStorageId(id: UUID) =
		// iqn.a.b.c:ID format required by iscsi standard - http://tools.ietf.org/html/rfc3720#section-12.4
		"${iscsiVolumePrefix}:${id}"
