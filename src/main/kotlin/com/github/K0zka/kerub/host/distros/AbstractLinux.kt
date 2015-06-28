package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import org.apache.sshd.ClientSession

public abstract class AbstractLinux : Distribution {
	override fun startMonitorProcesses(session: ClientSession, hostDynDao: HostDynamicDao) {

	}
}