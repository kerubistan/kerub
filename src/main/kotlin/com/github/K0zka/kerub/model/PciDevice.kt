package com.github.K0zka.kerub.model

import java.io.Serializable
import org.hibernate.search.annotations.Field

public class PciDevice(
		Field
		val device : String,
		Field
        val devClass : String,
		Field
        val vendor : String
                      ) : Serializable {

}