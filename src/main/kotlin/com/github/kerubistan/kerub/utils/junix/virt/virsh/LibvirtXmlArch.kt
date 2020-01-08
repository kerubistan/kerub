package com.github.kerubistan.kerub.utils.junix.virt.virsh

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

class LibvirtXmlArch {
	@get:XmlAttribute(name = "name")
	var name: String = ""
	@get:XmlElement(name = "wordsize")
	var wordsize: Int = 0
	@get:XmlElement(name = "emulator")
	var emulator: String = ""

}
