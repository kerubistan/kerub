package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

public trait DaoOperations {
	trait Add<T : Entity<I>, I> {
		fun add(entity : T) : I
	}

	trait Remove<T : Entity<I>, I> {
		fun remove(entity : T)
		fun remove(id : I)
	}

	trait Update<T : Entity<I>, I> {
		fun update(entity : T)
	}

	trait Read<T : Entity<I>, I> {
		fun get(id : I) : T?
	}

	/**
	 * List operations for DAOs.
	 */
	trait List<T : Entity<I>, I> {
		fun count() : Int
		fun list(
				start : Long = 0,
				limit : Long = java.lang.Long.MAX_VALUE,
				sort : String = "id") : kotlin.List<T>
	}

	/**
	 * Interface for the data that can be listed safely with a single operation.
	 * This should only be applied in cases where the number of entities is low.
	 */
	trait SimpleList<T : Any> {
		fun listAll() : kotlin.List<T>
	}

}