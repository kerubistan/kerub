package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

val separator = "--end"
val fields = "lv_uuid,lv_name,lv_path,lv_size,raid_min_recovery_rate,raid_max_recovery_rate,lv_layout,data_percent"
val fieldSeparator = ":"
val listOptions = "--noheadings --units b --separator=${fieldSeparator}"
