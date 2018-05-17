package com.github.kerubistan.kerub.services.socket

import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.Pool
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.services.ControllerConfigService
import com.github.kerubistan.kerub.services.HostDynamicService
import com.github.kerubistan.kerub.services.HostService
import com.github.kerubistan.kerub.services.PoolService
import com.github.kerubistan.kerub.services.VirtualMachineDynamicService
import com.github.kerubistan.kerub.services.VirtualMachineService
import com.github.kerubistan.kerub.services.VirtualNetworkService
import com.github.kerubistan.kerub.services.VirtualStorageDeviceDynamicService
import com.github.kerubistan.kerub.services.VirtualStorageDeviceService
import com.github.kerubistan.kerub.utils.inverse
import javax.ws.rs.Path
import kotlin.reflect.KClass

val slash = '/'

val services = mapOf<KClass<out Entity<out Any>>, KClass<*>>(
		Host::class to HostService::class,
		HostDynamic::class to HostDynamicService::class,
		VirtualMachine::class to VirtualMachineService::class,
		VirtualNetwork::class to VirtualNetworkService::class,
		VirtualMachineDynamic::class to VirtualMachineDynamicService::class,
		VirtualStorageDevice::class to VirtualStorageDeviceService::class,
		VirtualStorageDeviceDynamic::class to VirtualStorageDeviceDynamicService::class,
		ControllerConfig::class to ControllerConfigService::class,
		Pool::class to PoolService::class
)

fun pathFromJaxRsAnnotation(clazz: KClass<*>) =
		(clazz.annotations.first { it is Path } as Path).value

fun addSlashPrefix(path: String)
		= if (path.startsWith(slash)) path else "$slash$path"

fun addSlashPostFix(path: String)
		= if (path.endsWith(slash)) path else "$path$slash"

val channels = services.map { it.key to addSlashPrefix(addSlashPostFix(pathFromJaxRsAnnotation(it.value))) }.toMap()
val addressToEntity = channels.inverse()
