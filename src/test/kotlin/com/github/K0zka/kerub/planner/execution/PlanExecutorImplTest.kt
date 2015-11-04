package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateImage
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
public class PlanExecutorImplTest {
	@Mock
	var executor: HostCommandExecutor? = null

	@Test
	fun execute() {
		val host = Host(
				id = UUID.randomUUID(),
		        address = "127.0.0.1",
		        dedicated = true,
		        publicKey = ""
		               )
		val plan = Plan(
				state = OperationalState.fromLists(
						vms = listOf(),
						hosts = listOf(host),
						hostDyns = listOf(),
						vmDyns = listOf()
				                        ),
				steps = listOf(CreateImage(
						host = host,
						device = VirtualStorageDevice(
								size = "100 MB".toSize(),
								name = "foo"
						                             )
				                            )
				                )
		               )
		PlanExecutorImpl(executor!!).execute(plan)

		//Mockito.verify(executor)!!.execute(Matchers.eq(host) ?: host, Matchers.any() ?: Mockito.mock() )
	}
}