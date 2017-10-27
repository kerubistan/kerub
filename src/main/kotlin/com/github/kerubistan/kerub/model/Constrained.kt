package com.github.kerubistan.kerub.model

interface Constrained<T : Expectation> {
	val expectations: List<T>
}