package com.github.kerubistan.kerub.utils.junix.virt.virsh

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "capabilities")
class LibvirtXmlCapabilities {
	@get:XmlElement(name = "guest")
	var guests: MutableList<LibvirtXmlGuest> = mutableListOf()
}

