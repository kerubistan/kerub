package com.github.K0zka.kerub.services

import com.github.K0zka.kerub.model.Host
import java.util.UUID
import javax.ws.rs.Path

Path("/host")
public trait HostService : Listable<Host>, RestCrud<Host, UUID> {
}