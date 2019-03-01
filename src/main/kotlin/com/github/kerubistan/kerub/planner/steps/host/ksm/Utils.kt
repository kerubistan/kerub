package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.model.Host

fun controlKsm(host: Host, exec: HostCommandExecutor, on: Boolean) {
	exec.execute(host) {
		it.execute("echo ${if (on) 1 else 0} > /sys/kernel/mm/ksm/run")
	}
}