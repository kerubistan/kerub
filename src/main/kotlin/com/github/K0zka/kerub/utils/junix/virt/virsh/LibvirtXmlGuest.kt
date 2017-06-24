package com.github.K0zka.kerub.utils.junix.virt.virsh

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "guest")
class LibvirtXmlGuest {
	@get:XmlElement(name = "os_type") var osType: String? = null
	@get:XmlElement(name = "arch") var arch: LibvirtXmlArch? = null
}
