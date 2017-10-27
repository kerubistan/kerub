package com.github.kerubistan.kerub.model.paging

data class SearchResultPage<T>(override val start: Long = 0,
							   override val count: Long,
							   override val total: Long,
							   override val result: List<T>,
							   val searchby: String
) : ResultPage<T>