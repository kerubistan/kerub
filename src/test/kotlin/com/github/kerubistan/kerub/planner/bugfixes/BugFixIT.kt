package com.github.kerubistan.kerub.planner.bugfixes

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.k0zka.finder4j.backtrack.BacktrackServiceImpl
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.messages.PingMessage
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.OperationalStateBuilder
import com.github.kerubistan.kerub.planner.PlanExecutor
import com.github.kerubistan.kerub.planner.PlanViolationDetectorImpl
import com.github.kerubistan.kerub.planner.PlannerImpl
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

class BugFixIT {

	@Test
	fun thinLvm263() {
		val state: OperationalState = loadFrom("com.github.kerubistan.kerub.planner.bugfixes.thinlvm263")
		val builder = mock<OperationalStateBuilder>()
		whenever(builder.buildState()).thenReturn(state)
		val executor = mock<PlanExecutor>()
		PlannerImpl(
				backtrack = BacktrackServiceImpl(),
				builder = builder,
				executor = executor,
				violationDetector = PlanViolationDetectorImpl
		).onEvent(msg = PingMessage(sent = now()))

		// it is right that the planner did not create a plan, since the volume group did not have free capacity
		// measured
		verify(executor, never()).execute(any(), any())
	}

	@Test
	fun thinLvm263WithVgStatus() {
		val state: OperationalState = loadFrom("com.github.kerubistan.kerub.planner.bugfixes.thinlvm263vgstatus")
		val builder = mock<OperationalStateBuilder>()
		whenever(builder.buildState()).thenReturn(state)
		val executor = mock<PlanExecutor>()
		PlannerImpl(
				backtrack = BacktrackServiceImpl(),
				builder = builder,
				executor = executor,
				violationDetector = PlanViolationDetectorImpl
		).onEvent(msg = PingMessage(sent = now()))

		//and this case should create a plan because the free capacity of the vg is measured
		verify(executor).execute(any(), any())
	}

	@Test
	fun ubuntu16Nfs() {
		val state: OperationalState = loadFrom("com.github.kerubistan.kerub.planner.bugfixes.ubuntu16nfs")
		val builder = mock<OperationalStateBuilder>()
		whenever(builder.buildState()).thenReturn(state)
		val executor = mock<PlanExecutor>()
		PlannerImpl(
				backtrack = BacktrackServiceImpl(),
				builder = builder,
				executor = executor,
				violationDetector = PlanViolationDetectorImpl
		).onEvent(msg = PingMessage(sent = now()))

		// no solution and correctly as no nfs server installed on host
		// while all other solution is ruled out by config
		verify(executor, never()).execute(any(), any())

	}

	inline fun <reified T> load(resources: List<Resource>, cacheName: String, objectMapper: ObjectMapper) =
			resources.filter { it.uri.toString().contains(cacheName) }
					.map { objectMapper.readValue<T>(it.url) }

	private fun loadFrom(packageName: String): OperationalState {
		val objectMapper = createObjectMapper()
		val resources = PathMatchingResourcePatternResolver().getResources(
				"classpath:${packageName.replace('.', '/')}/**"
		).filter { it.filename.endsWith(".json") }

		return OperationalState.fromLists(
				hosts = load(resources, "hostCache", objectMapper),
				hostDyns = load(resources, "hostDynamicCache", objectMapper),
				hostCfgs = load(resources, "hostConfigCache", objectMapper),
				vms = load(resources, "vmCache", objectMapper),
				vmDyns = load(resources, "vmDynCache", objectMapper),
				vStorage = load(resources, "virtualStorageDeviceCache", objectMapper),
				vStorageDyns = load(resources, "virtualStorageDeviceDynamicCache", objectMapper),
				config = load<ControllerConfig>(resources, "controllerConfigCache", objectMapper)
						.singleOrNull() ?: ControllerConfig()
		)
	}

}