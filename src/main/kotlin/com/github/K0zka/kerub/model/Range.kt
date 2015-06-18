package com.github.K0zka.kerub.model

import java.io.Serializable

/**
 * A serializable range.
 */
data class Range<T> (final val min : T, final val max: T) : Serializable
