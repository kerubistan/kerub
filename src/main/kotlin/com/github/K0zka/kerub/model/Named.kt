package com.github.K0zka.kerub.model

import org.hibernate.search.annotations.Field

/**
 * Interface for entities that have a name.
 */
public interface Named {
    @Field
	val name : String
}