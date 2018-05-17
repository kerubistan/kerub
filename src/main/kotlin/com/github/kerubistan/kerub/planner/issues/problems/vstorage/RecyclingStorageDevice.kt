package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.issues.problems.Problem

data class RecyclingStorageDevice(val vStorage: VirtualStorageDevice) : Problem