package com.github.K0zka.kerub.data.dynamic

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import java.util.UUID

public trait HostDynamicDao : CrudDao<HostDynamic, UUID> {
}