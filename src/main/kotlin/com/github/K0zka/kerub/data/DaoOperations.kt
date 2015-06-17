package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

/**
 * Collection of common DAO operations to pick from when building DAO interfaces.
 */
public interface DaoOperations {

	/**
	 * Interface to allow adding entities to the backing data store.
	 */
	interface Add<T : Entity<I>, I> {
		fun add(entity : T) : I
	}

	/**
	 * Interface to allow removing entities from the backing data store.
	 */
	interface Remove<T : Entity<I>, I> {
		fun remove(entity : T)
		fun remove(id : I)
	}

	/**
	 * Interface to allow replacing/updating entities on the backing store.
	 */
	interface Update<T : Entity<I>, I> {
		fun update(entity : T)
	}

	/**
	 * Interface to allow retrieving entities from the backing store.
	 */
	interface Read<T : Entity<I>, I> {
		fun get(id : I) : T?
	}

	/**
	 * List operations for DAOs.
	 */
	interface List<T : Entity<I>, I> {
		/**
		 * Get the total count of entities.
		 */
		fun count() : Int
		/**
		 * List the entities.
		 */
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
