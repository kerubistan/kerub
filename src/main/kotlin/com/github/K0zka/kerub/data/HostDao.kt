package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Host
import java.util.UUID

public interface HostDao : ListableCrudDao<Host, UUID> {
}