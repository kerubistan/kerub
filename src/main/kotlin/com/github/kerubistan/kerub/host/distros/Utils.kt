package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.utils.junix.df.FilesystemInfo
import com.github.kerubistan.kerub.utils.junix.mount.FsMount

fun joinMountsAndDF(df: List<FilesystemInfo>, mnts: List<FsMount>): List<FsStorageCapability> {
	val mounts = mnts.map { it.mountPoint to it }.toMap()
	return df
			.map { fs -> mounts[fs.mountPoint]?.let { fs to it } }
			.filterNotNull()
			.map {
				mount ->
				FsStorageCapability(
						size = mount.first.free + mount.first.used,
						mountPoint = mount.first.mountPoint,
						fsType = mount.second.type
				)
			}
}
