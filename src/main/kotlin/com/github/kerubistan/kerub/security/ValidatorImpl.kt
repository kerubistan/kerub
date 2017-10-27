package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.data.hub.AnyAssetDao
import com.github.kerubistan.kerub.model.Asset

class ValidatorImpl(private val dao: AnyAssetDao) : Validator {

	override fun validate(asset: Asset) {
		asset.references().forEach {
			dao.getAll(it.key, it.value).forEach { check(it.owner == asset.owner) { "${it.id} has a different owner" } }
		}
	}
}