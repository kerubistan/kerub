package com.github.K0zka.kerub.controller

import java.io.Serializable
import java.util.UUID

public data class HostAssignedMessage(val hostId: UUID, val conrollerId: String) : Serializable