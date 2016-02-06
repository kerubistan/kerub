package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.paging.SearchResultPage
import com.github.K0zka.kerub.services.VirtualStorageDeviceService

class VirtualStorageDeviceServiceImpl(dao: VirtualStorageDeviceDao)
: VirtualStorageDeviceService,
		ListableBaseService<VirtualStorageDevice>(dao, "virtual disk") {
	override fun search(field: String, value: String, start: Long, limit: Long): SearchResultPage<VirtualStorageDevice> {
		val list = (dao as VirtualStorageDeviceDao).fieldSearch(
				field = field,
				value = value,
				start = start,
				limit = limit
		)
		return SearchResultPage(
				start = start,
				count = list.size.toLong(),
				result = list,
				total = dao.count().toLong(),
				searchby = field
		)
	}
}