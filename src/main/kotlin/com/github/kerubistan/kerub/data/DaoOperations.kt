package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.Named

/**
 * Collection of common DAO operations to pick from when building DAO interfaces.
 */
interface DaoOperations {

	/**
	 * Interface to allow adding entities to the backing data store.
	 */
	interface Add<T : Entity<I>, I> {
		fun add(entity: T): I
		fun addAll(entities: Collection<T>)
	}

	/**
	 * Interface to allow removing entities from the backing data store.
	 */
	interface Remove<T : Entity<I>, I> {
		fun remove(entity: T)
		fun remove(id: I)
	}

	/**
	 * Interface to allow replacing/updating entities on the backing store.
	 */
	interface Update<T : Entity<I>, I> {
		fun update(entity: T)
	}

	/**
	 * Interface to allow retrieving entities from the backing store.
	 */
	interface Read<T : Entity<I>, I> {
		operator fun get(id: I): T?
		operator fun get(ids: Collection<I>): List<T>
	}

	interface ByName<T : Named> {
		fun getByName(name: String, max: Int? = null): List<T>
		fun existsByName(name: String) = getByName(name, 1).isNotEmpty()
	}

	/**
	 * List operations for DAOs.
	 */
	interface PagedList<T : Entity<I>, I> {
		/**
		 * Get the total count of entities.
		 */
		fun count(): Int

		/**
		 * List the entities.
		 */
		fun list(
				start: Long = 0,
				limit: Int = Int.MAX_VALUE,
				sort: String = "id"): List<T>
	}

	/**
	 * Interface for the data that can be listed safely with a single operation.
	 * This should only be applied in cases where the number of entities is low.
	 */
	interface SimpleList<T : Any> {
		fun listAll(): List<T>
	}

	interface ListMany<I, T> {
		fun list(ids: Collection<I>): List<T>
	}

	interface SimpleSearch<T : Entity<*>> {
		fun fieldSearch(
				field: String,
				value: String,
				start: Long = 0,
				limit: Int = Int.MAX_VALUE
		): List<T>
	}

	interface Listen<I, T : Entity<I>> {
		fun listenCreate(action: (T) -> Boolean)
		fun listenCreate(id: I, action: (T) -> Boolean)
		fun listenUpdate(action: (T) -> Boolean)
		fun listenUpdate(id: I, action: (T) -> Boolean)
		fun listenDelete(action: (T) -> Boolean)
		fun listenDelete(id: I, action: (T) -> Boolean)
		/**
		 * Get the entity and perform the action
		 */
		fun waitFor(id: I, action: (T) -> Boolean)
	}

}
