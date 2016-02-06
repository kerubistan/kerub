package com.github.K0zka.kerub.model

interface Constrained<T : Expectation> {
	val expectations: List<T>
}