package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.model.Entity
import org.infinispan.Cache
import com.github.K0zka.kerub.data.ListableDao
import org.infinispan.query.Search
import com.github.K0zka.kerub.model.Host

public abstract open class ListableIspnDaoBase<T : Entity<I>, I> (cache: Cache<I, T>)
: IspnDaoBase<T, I>(cache), ListableDao<T, I> {

	abstract fun getEntityClass() : Class<T>

	var maxResults = 40
	override fun listAll(): List<T> {
		return Search.getQueryFactory(cache)!!
				.from(getEntityClass())!!
				.maxResults(maxResults)!!
				.build()!!
				.list<T>()!!
				.toList()
	}
}