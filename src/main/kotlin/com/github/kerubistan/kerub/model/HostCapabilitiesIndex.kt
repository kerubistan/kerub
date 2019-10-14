package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.kerubistan.kerub.utils.junix.compression.bzip2.BZip2
import com.github.kerubistan.kerub.utils.junix.compression.gzip.GZip
import com.github.kerubistan.kerub.utils.junix.compression.lz4.Lz4
import com.github.kerubistan.kerub.utils.junix.compression.xz.Xz
import java.io.Serializable

data class HostCapabilitiesIndex(val indexOf: HostCapabilities) : Serializable {
	@get:JsonIgnore
	val installedSoftwareByName by lazy(LazyThreadSafetyMode.PUBLICATION) {
		indexOf.installedSoftware.associateBy { it.name }
	}

	val installedPackageNames by lazy(LazyThreadSafetyMode.PUBLICATION) {
		indexOf.installedSoftware.map { it.name }.toSet()
	}

	@get:JsonIgnore
	val storageCapabilitiesById by lazy { indexOf.storageCapabilities.associateBy { it.id } }

	@get:JsonIgnore
	val lvmStorageCapabilitiesByVolumeGroupName by lazy {
		indexOf.storageCapabilities.filterIsInstance<LvmStorageCapability>().associateBy { it.volumeGroupName }
	}

	companion object {
		private val compressionPackages = listOf(GZip, BZip2, Lz4, Xz)
	}

	@get:JsonIgnore
	val compressionCapabilities by lazy {
		compressionPackages.filter { it.available(indexOf) }.map { it.format }.toSet()
	}

}