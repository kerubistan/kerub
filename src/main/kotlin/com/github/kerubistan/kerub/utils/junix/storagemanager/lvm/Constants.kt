package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

const val separator = "--end"
const val fields = "lv_uuid,lv_name,lv_path,lv_size,raid_min_recovery_rate,raid_max_recovery_rate,lv_layout,data_percent"
const val vgFields = "vg_uuid,vg_name,vg_size,vg_free,vg_extent_count,vg_free_count"
const val fieldSeparator = ":"
const val listOptions = "--noheadings --units b --separator=$fieldSeparator"
