package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.stat.BasicBalanceReport
import com.github.K0zka.kerub.data.stat.StatisticsDao
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class UsageStatisticsServiceImplTest {

	private val statDao: StatisticsDao = mock()

	@Test
	fun basicBalanceReport() {
		val report = BasicBalanceReport(
				totalHosts = 1,
				hostsOnline = 1,
				totalVms = 1,
				totalDedicatedVmCpus = 0,
				totalVmCpus = 1,
				totalHostCpus = 1,
				totalHostMemory = "1 GB".toSize(),
				totalDiskStorageActual = "1 GB".toSize(),
				totalDiskStorageRequested = "1 GB".toSize(),
				totalHostStorage = "1 GB".toSize(),
				totalHostStorageFree = "500MB".toSize(),
				totalMaxVmMemory = "1 GB".toSize(),
				totalMinVmMemory = "1 GB".toSize()
		)
		whenever(statDao.basicBalanceReport()).thenReturn(report)

		val balanceReport = UsageStatisticsServiceImpl(statDao).basicBalanceReport()
		kotlin.test.assertEquals(report, balanceReport)
		verify(statDao).basicBalanceReport()
	}

}