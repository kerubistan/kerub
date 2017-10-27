package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import java.util.UUID

interface VirtualStorageDeviceDao : ListableCrudDao<VirtualStorageDevice, UUID>,
		DaoOperations.SimpleSearch<VirtualStorageDevice>, AssetDao<VirtualStorageDevice>