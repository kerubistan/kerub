package com.github.K0zka.kerub.controller

import java.io.Serializable

public interface InterController {
	fun sendToController(controllerId : String, msg : Serializable)
	fun broadcast(msg : Serializable)
}