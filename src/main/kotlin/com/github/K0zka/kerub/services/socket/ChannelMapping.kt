package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.controller.config.ControllerConfig
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.services.ControllerConfigService
import com.github.K0zka.kerub.services.ControllerService
import com.github.K0zka.kerub.services.HostDynamicService
import com.github.K0zka.kerub.services.HostService
import com.github.K0zka.kerub.services.VirtualMachineDynamicService
import com.github.K0zka.kerub.services.VirtualMachineService
import com.github.K0zka.kerub.services.VirtualNetworkService
import com.github.K0zka.kerub.services.VirtualStorageDeviceDynamicService
import com.github.K0zka.kerub.services.VirtualStorageDeviceService
import com.github.K0zka.kerub.utils.inverse
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
		ControllerConfig::class to ControllerConfigService::class
)

fun pathFromJaxRsAnnotation(clazz: KClass<*>) =
		(clazz.annotations.first { it is Path } as Path).value

fun addSlashPrefix(path: String)
		= if (path.startsWith(slash)) path else "$slash$path"

fun addSlashPostFix(path: String)
		= if (path.endsWith(slash)) path else "$path$slash"

val channels = services.map { it.key to addSlashPrefix(addSlashPostFix(pathFromJaxRsAnnotation(it.value))) }.toMap()
val addressToEntity = channels.inverse()
