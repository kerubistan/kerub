package com.github.K0zka.kerub.model

import java.util.UUID
import javax.xml.bind.annotation.XmlRootElement
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.Key
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.DocumentId

/**
 *
 */
Indexed
XmlRootElement(name = "host")
data class Host : Entity<UUID> {
	DocumentId
	override var id : UUID? = null
	Field
	var address : String? = null
	Field
	var publicKey : String? = null
	Field
	var dedicated : Boolean = true

}