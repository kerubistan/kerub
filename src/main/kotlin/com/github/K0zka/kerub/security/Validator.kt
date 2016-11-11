package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.model.Asset

interface Validator {
	fun validate(asset: Asset)
}