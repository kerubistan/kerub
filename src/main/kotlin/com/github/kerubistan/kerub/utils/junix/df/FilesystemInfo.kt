package com.github.kerubistan.kerub.utils.junix.df

import java.math.BigInteger

data class FilesystemInfo(val mountPoint: String, val used: BigInteger, val free: BigInteger)