package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.VirtualNetwork
import java.util.UUID

interface VirtualNetworkDao : ListableCrudDao<VirtualNetwork, UUID>, AssetDao<VirtualNetwork>