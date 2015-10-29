package com.github.K0zka.kerub.model.paging

interface ResultPage<T> {
	val start: Long
	val count: Long
	val total: Long
	val result: List<T>
}