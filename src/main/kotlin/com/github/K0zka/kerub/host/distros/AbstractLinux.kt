package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.junix.vmstat.VmStat
import org.apache.sshd.ClientSession

public abstract class AbstractLinux : Distribution {

	companion object {
		val logger = getLogger(AbstractLinux::class)
	}

	override fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao) {
		val id = host.id
		VmStat.vmstat(session, { event ->
			val hostDyn = hostDynDao.get(id)
			logger.debug("{} vmstat : {}\t{}\t{}", id, event.userCpu, event.systemCpu, event.idleCpu)
			if (hostDyn == null) {
				val newHostDyn = HostDynamic(
						id = id,
						status = HostStatus.Up,
						idleCpu = event.idleCpu,
						systemCpu = event.systemCpu,
						userCpu = event.userCpu
				                            )
				hostDynDao.add(newHostDyn)
			} else {
				val newHostDyn = hostDyn.copy(
						status = HostStatus.Up,
						idleCpu = event.idleCpu,
						systemCpu = event.systemCpu,
						userCpu = event.userCpu
				                             )
				hostDynDao.update(newHostDyn)
			}

		})
	}
}