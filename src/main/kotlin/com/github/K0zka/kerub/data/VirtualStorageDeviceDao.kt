package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.VirtualStorageDevice
import java.util.UUID

interface VirtualStorageDeviceDao : ListableCrudDao<VirtualStorageDevice, UUID>,
		DaoOperations.SimpleSearch<VirtualStorageDevice>, AssetDao<VirtualStorageDevice>