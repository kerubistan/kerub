package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.services.VirtualNetworkService

class VirtualNetworkServiceImpl(dao: VirtualNetworkDao)
: ListableBaseService<VirtualNetwork>(dao, "virtual network"), VirtualNetworkService