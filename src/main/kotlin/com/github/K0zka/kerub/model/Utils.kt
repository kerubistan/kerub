package com.github.K0zka.kerub.model

import java.util.Collections

/*
 * These utility methods are intended to be temporary until
 * kotlin gives better support
 * https://youtrack.jetbrains.com/issue/KT-6682
 */

fun serializableListOf<T>() : List<T> = Collections.emptyList()
