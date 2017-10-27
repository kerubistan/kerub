package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.VirtualNetwork
import java.util.UUID

interface VirtualNetworkDao : ListableCrudDao<VirtualNetwork, UUID>, AssetDao<VirtualNetwork>