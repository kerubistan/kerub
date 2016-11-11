package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.security.AccessController
import com.github.K0zka.kerub.services.VirtualNetworkService

class VirtualNetworkServiceImpl(accessController: AccessController, dao: VirtualNetworkDao)
: AbstractAssetService<VirtualNetwork>(accessController, dao, "virtual network"), VirtualNetworkService