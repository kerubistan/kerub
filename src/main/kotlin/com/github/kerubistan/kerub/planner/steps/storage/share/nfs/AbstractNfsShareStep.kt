package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

abstract class AbstractNfsShareStep : AbstractNfsStep() {
	abstract val directory: String
}