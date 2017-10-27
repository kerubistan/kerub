package com.github.kerubistan.kerub.model.controller.config

import java.io.Serializable

data class ArchiverConfig(
		/**
		 * A cron expression on when to run the compression jobs
		 */
		val compressDayJobCron: String = "0 0 * * *",
		/**
		 * Keep a number of days uncompressed
		 */
		val keepDays: Int = 7
) : Serializable