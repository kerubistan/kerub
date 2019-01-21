package com.github.kerubistan.kerub.model.collection


internal fun DataCollection<*, *, *>.validate() {
	dynamic?.apply {
		check(id == stat.id) { "stat (${stat.id}) and dyn ($id) ids must match" }
	}
}
