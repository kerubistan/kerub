package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

const val separator = "--end"
const val fields =
		"vg_name,lv_uuid,lv_name,lv_path,lv_size,raid_min_recovery_rate,raid_max_recovery_rate,lv_layout,data_percent"
const val vgFields = "vg_uuid,vg_name,vg_size,vg_free,vg_extent_count,vg_free_count"
const val fieldSeparator = ":"
const val listOptions = "--noheadings --units b --separator=$fieldSeparator"

val vgName = fieldPosition("vg_name")
val lvUuid = fieldPosition("lv_uuid")
val lvName = fieldPosition("lv_name")
val lvPath = fieldPosition("lv_path")
val lvSize = fieldPosition("lv_size")
val lvLayout = fieldPosition("lv_layout")
val raidMinRecoveryRate = fieldPosition("raid_min_recovery_rate")
val raidMaxRecoveryRate = fieldPosition("raid_max_recovery_rate")
val dataPercent = fieldPosition("data_percent")
val nrOfLvsOutputColumns = fields.split(",").size

private fun fieldPosition(columnName: String) = fields.split(",").indexOf(columnName)
