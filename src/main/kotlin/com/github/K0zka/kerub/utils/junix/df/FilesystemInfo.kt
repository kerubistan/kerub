package com.github.K0zka.kerub.utils.junix.df

import java.math.BigInteger

data class FilesystemInfo(val mountPoint: String, val used: BigInteger, val free: BigInteger)