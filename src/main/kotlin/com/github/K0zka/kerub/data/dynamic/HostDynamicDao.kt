package com.github.K0zka.kerub.data.dynamic

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import java.util.UUID

interface HostDynamicDao : CrudDao<HostDynamic, UUID> {
}