package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.nhaarman.mockito_kotlin.eq
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

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

        val closure: (ClientSession) -> Unit = {}
        Mockito.verify(exec!!).execute(Mockito.eq(host) ?: host, Mockito.any() ?: closure)
        val change: (HostDynamic) -> HostDynamic = { it }
        Mockito.verify(hostDynDao!!).update(eq(host.id) ?: host.id, Mockito.any() ?: change)

    }
}