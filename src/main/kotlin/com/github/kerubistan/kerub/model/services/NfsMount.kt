package com.github.kerubistan.kerub.model.services

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("nfs-mount")
data class NfsMount(val remoteHostId: UUID, val remoteDirectory: String, val localDirectory: String) : HostService