package com.github.K0zka.kerub.model

public interface Constrained<T : Expectation> {
	val expectations: List<T>
}