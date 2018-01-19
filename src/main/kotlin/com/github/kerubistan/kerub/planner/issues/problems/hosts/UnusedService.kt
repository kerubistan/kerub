package com.github.kerubistan.kerub.planner.issues.problems.hosts

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.planner.issues.problems.Problem

data class UnusedService(val host: Host, val service: HostService) : Problem