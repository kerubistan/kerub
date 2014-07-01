package com.github.K0zka.kerub.model

import java.util.UUID
import java.util.Date
import javax.xml.bind.annotation.XmlRootElement

XmlRootElement(name = "project")
public class Project : Entity<UUID>{
	override var id: UUID? = null
	var name : String? = null
	var description : String? = null
	var created : Date? = null
	var expectations : List<Expectation> = listOf()
}