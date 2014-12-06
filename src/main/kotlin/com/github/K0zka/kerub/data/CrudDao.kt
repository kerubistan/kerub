package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

public trait CrudDao<T : Entity<I>, I>
: DaoOperations.Read<T, I>, DaoOperations.Add<T, I>, DaoOperations.Remove<T, I>, DaoOperations.Update<T, I> {
}