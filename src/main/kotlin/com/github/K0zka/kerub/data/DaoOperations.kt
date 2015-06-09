package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

public interface DaoOperations {
	interface Add<T : Entity<I>, I> {
		fun add(entity : T) : I
	}

	interface Remove<T : Entity<I>, I> {
		fun remove(entity : T)
		fun remove(id : I)
	}

	interface Update<T : Entity<I>, I> {
		fun update(entity : T)
	}

	interface Read<T : Entity<I>, I> {
		fun get(id : I) : T?
	}

	/**
	 * List operations for DAOs.
	 */
	interface List<T : Entity<I>, I> {
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
	interface SimpleList<T : Any> {
		fun listAll() : kotlin.List<T>
	}

}