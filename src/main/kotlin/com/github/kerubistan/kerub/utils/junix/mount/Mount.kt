package com.github.kerubistan.kerub.utils.junix.mount

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.flag
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.substringBetween
import org.apache.sshd.client.session.ClientSession

object Mount : OsCommand {
	//no known distribution without a mount command
	override fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities?.os == OperatingSystem.Linux
					|| (
					hostCapabilities?.os == OperatingSystem.BSD
							&& hostCapabilities.distribution?.name == "NetBSD"
					)

	fun listMounts(session: ClientSession): List<FsMount> =
			session.executeOrDie("mount").lines().map {
				FsMount(
						device = it.substringBefore(" on ").trim(),
						mountPoint = it.substringBetween(" on ", " type ").trim(),
						options = it.substringBetween("(", ")").split(','),
						type = it.substringBetween(" type ", "(").trim()
				)
			}

	fun mount(session: ClientSession,
			  deviceSpec: String,
			  mountPoint: String,
			  write: Boolean = true,
			  verbose: Boolean = true) {
		session.executeOrDie("mount ${verbose.flag("-v")} $deviceSpec $mountPoint -o ${write.flag("rw", "ro")} ")
	}

	fun unmount(session: ClientSession, mountPoint: String) {
		session.executeOrDie("umount $mountPoint} ")
	}

	fun mountNfs(session: ClientSession, address: String, remoteDir: String, mountPoint: String,
				 write: Boolean = true, verbose: Boolean = true) =
			mount(session, "$address:$remoteDir", mountPoint, write, verbose)

}