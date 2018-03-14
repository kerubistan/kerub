package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

abstract class AbstractNfsShareStep : AbstractNfsStep() {
	abstract val directory: String
}