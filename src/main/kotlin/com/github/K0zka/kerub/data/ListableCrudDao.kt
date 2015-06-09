package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

/**
 * Interface for DAO's with the base CRUD and listing operations.
 */
public interface ListableCrudDao<T : Entity<I>, I> : CrudDao<T, I>, DaoOperations.List<T, I> {
}