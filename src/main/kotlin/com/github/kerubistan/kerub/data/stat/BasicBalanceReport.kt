package com.github.kerubistan.kerub.data.stat

import java.math.BigInteger

data class BasicBalanceReport(
		//systems
		// @formatter:off
		val totalHosts: Int, // number of hosts
		val hostsOnline: Int, //hosts in Up state
		val totalVms: Int, // number of virtual machines
		// memory
		val totalHostMemory: BigInteger, // sum of all the memory in hosts
		val totalMinVmMemory: BigInteger, // sum of the min memory
		val totalMaxVmMemory: BigInteger, // sum of the max memory
		// CPUs
		val totalHostCpus: Int, // sum of all host CPUs
		val totalVmCpus: Int, // sum of all vm cpus
		val totalDedicatedVmCpus: Int, // sum of vm cpus that are have dedicated requirement
		// storage
		val totalHostStorage: BigInteger, // sum of all storage capabilities in all hosts
		val totalHostStorageFree: BigInteger, // sum of all storage capabilities in all hosts
		val totalDiskStorageRequested: BigInteger, // sum of all virtual disk size
		val totalDiskStorageActual: BigInteger        // sum of the actual disk space taken by virtual disks
		// @formatter:on
)