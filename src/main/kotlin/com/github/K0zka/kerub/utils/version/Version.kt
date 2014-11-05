package com.github.K0zka.kerub.utils.version

import com.github.K0zka.kerub.utils.getLogger
import java.util.regex.Pattern
import com.github.K0zka.kerub.utils.elemOrNull
import java.io.Serializable

public class Version (val major : String, val minor: String?, val build : String?) : Serializable {
	class object {
		private val logger = getLogger(javaClass<Version>())

		private val empty = ""

		private val versionSplitter = Pattern.compile("\\.|_|\\-")

		public fun fromVersionString(versionStr : String) : Version {
			val split = versionSplitter.split(versionStr)
			return Version(split[0], split.elemOrNull(1), split.elemOrNull(2))
		}
	}
	private inline fun dot(component : String?) : String? {
		if(component == null) {
			return ".${component}"
		} else {
			return null
		}
	}
	override fun toString() : String {
		return "$major${dot(minor) ?: empty}${dot(build) ?: empty}"
	}
}