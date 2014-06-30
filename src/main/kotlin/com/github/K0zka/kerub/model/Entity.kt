package com.github.K0zka.kerub.model

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Generic entity type.
 * The only sure thing about an entity is that it has got an ID
 */
JsonTypeInfo(use=JsonTypeInfo.Id.NAME , include=JsonTypeInfo.As.PROPERTY, property="@type")
data trait Entity<T> : Serializable {
	var id : T?
}