package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.model.Asset

interface Validator {
	fun validate(asset: Asset)
}