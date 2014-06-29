package com.github.K0zka.kerub.model

import java.io.Serializable

data trait Entity<T> : Serializable {
	var id : T?
}