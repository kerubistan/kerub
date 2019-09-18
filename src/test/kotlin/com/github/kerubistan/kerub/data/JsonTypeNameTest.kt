package com.github.kerubistan.kerub.data

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.kerubistan.kroki.collections.join
import org.junit.Test
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Point here is: whatever class / interface in the package have any
 * JsonSubTypes annotation:
 * - All subtypes must be listed
 * - All subtypes must have a JsonTypeName annotation - therefore not the class name is used as a value
 * - And the actual value of that annotation must follow a naming pattern:
 * 		small letters with line separation, whitespaces not acceptable or whitespaces not acceptable
 */
class JsonTypeNameTest {

	val pattern = "([a-z]|-)+".toRegex()

	@ExperimentalStdlibApi
	@Test
	fun checkAnnotations() {
		val reflections = Reflections("com.github.kerubistan.kerub.model", SubTypesScanner(false))
		reflections.allTypes
				.map { Class.forName(it).kotlin }
				.filter { it.java.isInterface }
				.mapNotNull {
					if (it.hasAnnotation<JsonSubTypes>()) {
						it to it.java.getAnnotation(JsonSubTypes::class.java)
					} else null
				}
				.forEach { (clazz, annotation) ->
					val subtypes = reflections.getSubTypesOf(clazz.java)
							.map { setOf(it) + reflections.getSubTypesOf(it) }
							.join()
							.filter { !it.isInterface && !it.kotlin.isAbstract }
							.toSet()
					val annotationSubtypes = annotation.value.map { it.value.java }.toSet()

					val sortedSubtypes = subtypes.map { it.name }.toList().sorted()
					val sortedAnnotationSubtypes = annotationSubtypes.map { it.name }.toList().sorted()
					assertEquals(
							sortedSubtypes.joinToString("\n"),
							sortedAnnotationSubtypes.joinToString("\n"),
							"Annotations on $clazz"
					)

					subtypes.forEach { subtype ->
						assertTrue("JsonTypeName annotation required on ${subtype.name}") {
							subtype.kotlin.hasAnnotation<JsonTypeName>()
						}
						assertTrue("JsonTypeName must match pattern on ${subtype.name} : ") {
							subtype.kotlin.findAnnotation<JsonTypeName>()!!.value.matches(pattern)
						}
					}

				}
	}
}