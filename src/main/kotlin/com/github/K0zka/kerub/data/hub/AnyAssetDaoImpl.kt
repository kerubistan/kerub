package com.github.K0zka.kerub.data.hub

import com.github.K0zka.kerub.data.AssetDao
import com.github.K0zka.kerub.data.DaoOperations
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.Asset
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.model.VirtualStorageDevice
import java.util.UUID
import kotlin.reflect.KClass

class AnyAssetDaoImpl(
		vmDao: VirtualMachineDao,
		vStorageDao: VirtualStorageDeviceDao,
		vNetDao: VirtualNetworkDao
) : AnyAssetDao {

	private val daos = mapOf<KClass<*>, AssetDao<*>>(
			VirtualMachine::class to vmDao,
			VirtualNetwork::class to vNetDao,
			VirtualStorageDevice::class to vStorageDao
	)

	override fun <T : Asset> get(clazz: KClass<T>, id: UUID): T =
			requireNotNull(getDao(clazz) [id]) {
				"$clazz with $id not found"
			}

	override fun <T : Asset> getAll(clazz: KClass<T>, ids: Collection<UUID>): List<T> =
			getDao(clazz)[ids]

	internal fun <T : Asset> getDao(clazz: KClass<T>) = requireNotNull(
			requireNotNull(daos[clazz]) { "dao not found for $clazz" } as DaoOperations.Read<T, UUID>)
}