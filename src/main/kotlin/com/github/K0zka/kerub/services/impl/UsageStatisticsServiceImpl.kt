package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.stat.BasicBalanceReport
import com.github.K0zka.kerub.data.stat.StatisticsDao
import com.github.K0zka.kerub.services.UsageStatisticsService

class UsageStatisticsServiceImpl(private val statisticsDao: StatisticsDao) : UsageStatisticsService {
	override fun basicBalanceReport(): BasicBalanceReport
			= statisticsDao.basicBalanceReport()
}