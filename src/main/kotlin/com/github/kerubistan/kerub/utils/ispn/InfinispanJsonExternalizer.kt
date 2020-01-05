package com.github.kerubistan.kerub.utils.ispn

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.utils.createObjectMapper
import org.infinispan.commons.marshall.AdvancedExternalizer
import java.io.ObjectInput
import java.io.ObjectOutput

class InfinispanJsonExternalizer : AdvancedExternalizer<Entity<*>> {

	companion object {
		private val mapper = createObjectMapper(prettyPrint = false)
	}

	override fun getTypeClasses(): Set<Class<out Entity<*>>> =
			(Entity::class.annotations
					.single { it is JsonSubTypes } as JsonSubTypes)
					.value.map { it.value.java as Class<out Entity<*>> }
					.toSet()

	override fun writeObject(output: ObjectOutput, entity: Entity<*>?) {
		mapper.writeValue(output, entity)
	}

	override fun readObject(input: ObjectInput): Entity<*> = mapper.readValue(input, Entity::class.java)

	override fun getId(): Int = 6666
}