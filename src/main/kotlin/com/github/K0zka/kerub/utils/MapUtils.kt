package com.github.K0zka.kerub.utils

import java.util.HashMap

public inline fun <K, V> Map<K, V>.toMutable() : MutableMap<K, V> {
	return HashMap(this)
}

public fun <K, V> Map<K, V>.toPairList() : List<Pair<K, V>> {
	return this.entrySet().toList().map { it.toPair() }.toList()
}

public fun <K, V> Map<K, V>.plus (other : Map<K, V>) : Map<K, V>  {
	val mutableMap = this.toMutable()
	mutableMap.putAll(other)
	return mapOf(*mutableMap.toPairList().copyToArray())
}

public fun <K, V> Map<K, V>.plus (pair : Pair<K, V>) : Map<K, V>  {
	val mutableMap = this.toMutable()
	mutableMap.put(pair.first, pair.second)
	return mapOf(*mutableMap.toPairList().copyToArray())
}