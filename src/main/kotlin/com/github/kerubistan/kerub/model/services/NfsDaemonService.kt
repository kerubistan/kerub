package com.github.kerubistan.kerub.model.services

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("nfs-daemon")
data class NfsDaemonService(val running: Boolean = true) : HostService