package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.security.AssetAccessController
import com.github.K0zka.kerub.services.VirtualNetworkService

class VirtualNetworkServiceImpl(accessController: AssetAccessController, dao: VirtualNetworkDao)
	: AbstractAssetService<VirtualNetwork>(accessController, dao, "virtual network"), VirtualNetworkService