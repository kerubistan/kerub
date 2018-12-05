package com.github.kerubistan.kerub.model

import java.io.Serializable
import java.math.BigInteger

data class GvinumStorageCapabilityDrive (val name : String, val device : String, val size : BigInteger) : Serializable