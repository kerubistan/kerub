package com.github.K0zka.kerub.utils

import java.util.HashMap

public fun <K, V> Map<K, V>.toMutable() : MutableMap<K, V> {
	return HashMap(this)
}

public fun <K, V> Map<K, V>.toPairList() : List<Pair<K, V>> {
	return this.entrySet().toList().map { it.toPair() }.toList()
}
