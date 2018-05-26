package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualNetworkDao
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.VirtualNetworkService

class VirtualNetworkServiceImpl(
		accessController: AssetAccessController,
		dao: VirtualNetworkDao,
		private val vmDao: VirtualMachineDao
) : AbstractAssetService<VirtualNetwork>(accessController, dao, "virtual network"), VirtualNetworkService {
	override fun beforeRemove(entity: VirtualNetwork) {
		check(vmDao.listByAttachedNetwork(entity.id).isEmpty())
	}
}