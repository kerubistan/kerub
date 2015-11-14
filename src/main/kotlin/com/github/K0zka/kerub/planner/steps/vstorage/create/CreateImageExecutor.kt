package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.utils.junix.qemu.QemuImg

public class CreateImageExecutor(val exec : HostCommandExecutor) : StepExecutor<CreateImage>{
	override fun execute(step: CreateImage) {
		exec.execute(step.host, {
			QemuImg.create(
					session = it,
					format = VirtualDiskFormat.raw, //TODO
			        path = "/var/${step.device.id}", //TODO
			        size = step.device.size
			              )
		})
	}
}