package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.stat.BasicBalanceReport
import com.github.kerubistan.kerub.data.stat.StatisticsDao
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.MB
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
				totalHostMemory = 1.GB,
				totalDiskStorageActual = 1.GB,
				totalDiskStorageRequested = 1.GB,
				totalHostStorage = 1.GB,
				totalHostStorageFree = 500.MB,
				totalMaxVmMemory = 1.GB,
				totalMinVmMemory = 1.GB
		)
		whenever(statDao.basicBalanceReport()).thenReturn(report)

		val balanceReport = UsageStatisticsServiceImpl(statDao).basicBalanceReport()
		kotlin.test.assertEquals(report, balanceReport)
		verify(statDao).basicBalanceReport()
	}

}