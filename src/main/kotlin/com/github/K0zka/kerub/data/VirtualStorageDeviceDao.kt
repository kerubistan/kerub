package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.VirtualStorageDevice
import java.util.UUID

public interface VirtualStorageDeviceDao : ListableCrudDao<VirtualStorageDevice, UUID>,
		DaoOperations.SimpleSearch<VirtualStorageDevice>