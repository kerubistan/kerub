package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.Host

fun controlKsm(host: Host, exec: HostCommandExecutor, on: Boolean) {
	exec.execute(host, {
		it.execute("echo ${if (on) 1 else 0} > /sys/kernel/mm/ksm/run")
	})
}