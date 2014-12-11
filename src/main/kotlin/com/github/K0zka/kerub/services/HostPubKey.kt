package com.github.K0zka.kerub.services

import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlValue

XmlRootElement(name = "HostPubKey")
public class HostPubKey(XmlAttribute(name = "algorithm")
                        val algorithm: String?,
                        XmlAttribute(name = "format")
                        val format: String?,
                        XmlValue
                        var fingerprint: String) {

}