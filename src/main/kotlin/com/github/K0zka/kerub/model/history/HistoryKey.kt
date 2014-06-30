package com.github.K0zka.kerub.model.history

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

public final class HistoryKey<out T> (val key: T, val time: Long = System.currentTimeMillis()) : Serializable {

	override fun hashCode(): Int {
		return key.hashCode() + time.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		return other is HistoryKey<*> && (other as HistoryKey<*>).key == key && (other as HistoryKey<*>).time == time
	}

	override fun toString(): String {
		return "${key.toString()} - ${SimpleDateFormat("yyyy-MM-dd hh:mm:SS").format(Date(time))}"
	}
}