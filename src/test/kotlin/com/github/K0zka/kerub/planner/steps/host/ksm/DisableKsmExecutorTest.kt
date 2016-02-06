package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import nl.komponents.kovenant.any
import org.apache.activemq.artemis.api.core.client.ClientSession
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class DisableKsmExecutorTest {

    @Mock
    var exec: HostCommandExecutor? = null
    @Mock
    var hostDynDao: HostDynamicDao? = null

    val host = Host(
            id = UUID.randomUUID(),
            address = "host-1.example.com",
            dedicated = true,
            publicKey = ""
    )

    @Test
    fun execute() {
        DisableKsmExecutor(exec!!, hostDynDao!!).execute(DisableKsm(
                host = host
        ))

        val closure: (org.apache.sshd.ClientSession) -> Unit = {}
        Mockito.verify(exec!!).execute(Mockito.eq(host) ?: host, Mockito.any() ?: closure)
        val change: (HostDynamic) -> HostDynamic = { it }
        Mockito.verify(hostDynDao!!).update(eq(host.id) ?: host.id, Mockito.any() ?: change)

    }
}