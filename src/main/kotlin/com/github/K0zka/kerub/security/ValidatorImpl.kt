package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.data.AssetDao
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.model.VirtualStorageDevice
import kotlin.reflect.KClass

class ValidatorImpl(
		private val vmDao: VirtualMachineDao,
		private val vnetDao: VirtualNetworkDao,
		private val vstorageDao: VirtualStorageDeviceDao) : Validator {

	private val daos = mapOf<KClass<*>, AssetDao<*>>(
			VirtualMachine::class to vmDao,
			VirtualNetwork::class to vnetDao,
			VirtualStorageDevice::class to vstorageDao
	)

	override fun validate(asset: Asset) {

		asset.references().forEach {
			val dao = requireNotNull(daos[it.key]) { "${it.key} does not have a DAO set in validator" }
			//on purpose let's not tell in the exception who the owner is, the exception at the end will
			//be presented and the owner ID could be a privacy leak
			dao.get(it.value).forEach { check(it.owner == asset.owner) { "${it.id} has a different owner" } }
		}

	}
}