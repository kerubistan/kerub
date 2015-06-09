package com.github.K0zka.kerub.data

import java.util.UUID
import com.github.K0zka.kerub.model.Host

public interface HostDao : ListableCrudDao<Host, UUID> {
}