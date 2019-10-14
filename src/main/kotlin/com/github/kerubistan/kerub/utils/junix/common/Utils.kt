package com.github.kerubistan.kerub.utils.junix.common

import com.github.kerubistan.kerub.model.HostCapabilities

fun HostCapabilities?.anyPackageNamed(name: String) = this?.index?.installedPackageNames?.contains(name) ?: false