package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.stat.BasicBalanceReport
import com.github.kerubistan.kerub.data.stat.StatisticsDao
import com.github.kerubistan.kerub.services.UsageStatisticsService

class UsageStatisticsServiceImpl(private val statisticsDao: StatisticsDao) : UsageStatisticsService {
	override fun basicBalanceReport(): BasicBalanceReport
			= statisticsDao.basicBalanceReport()
}