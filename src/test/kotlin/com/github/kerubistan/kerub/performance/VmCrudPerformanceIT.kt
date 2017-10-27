package com.github.kerubistan.kerub.performance

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.VirtualMachineService
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.avgBy
import nl.komponents.kovenant.task
import org.junit.After
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.util.Random
import java.util.UUID

class VmCrudPerformanceIT {

	var totalVms = listOf<UUID>()

	@Before
	fun check() {
		Assume.assumeTrue(System.getProperty("kerub.performancetest") != null)
	}

	@Test
	fun run() {
		val random = Random()
		val client = createClient()
		client.login("user")
		client.runRestAction(VirtualMachineService::class) {
			vmService ->
			for (i in 1..100) {

				print("$i,")
				val newVms = (1..1000).map {
					task {
						val id = UUID.randomUUID()
						val start = System.currentTimeMillis()
						vmService.add(testVm.copy(id = id, name = "vm-$i"))
						val end = System.currentTimeMillis()
						Triple(id, start, end)
					}
				}.map { it.get() }

				print(newVms.avgBy { (it.third - it.second).toInt() })
				print(',')

				totalVms += newVms.map { it.first }

				val vmGets = (1..10000).map { totalVms.get(random.nextInt(totalVms.size)) }.map {
					task {
						val start = System.currentTimeMillis()
						vmService.getById(it)
						val end = System.currentTimeMillis()
						start to end
					}
				}.map { it.get() }
				println(vmGets.avgBy { (it.second - it.first).toInt() })
			}
		}
	}

	@After
	fun cleanup() {
		val client = createClient()
		client.login("user")
		client.runRestAction(VirtualMachineService::class) {
			vmService ->
			totalVms.forEach {
				vmService.delete(it)
			}
		}
	}

}