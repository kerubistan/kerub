package com.github.kerubistan.kerub.utils.storage

import java.util.UUID

const val iscsiDefaultUser = "kerubagent"

private const val iscsiVolumePrefix: String = "iqn.io.github.kerub"

fun iscsiStorageId(id: UUID) =
		// iqn.a.b.c:ID format required by iscsi standard - http://tools.ietf.org/html/rfc3720#section-12.4
		"$iscsiVolumePrefix:$id"
