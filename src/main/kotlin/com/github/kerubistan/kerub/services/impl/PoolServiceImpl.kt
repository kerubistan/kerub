package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.PoolDao
import com.github.kerubistan.kerub.model.Pool
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.PoolService

class PoolServiceImpl(
		dao: PoolDao,
		accessController: AssetAccessController
) : PoolService, AbstractAssetService<Pool>(accessController, dao, "pool")