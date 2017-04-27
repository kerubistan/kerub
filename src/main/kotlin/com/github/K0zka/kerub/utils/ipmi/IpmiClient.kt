package com.github.K0zka.kerub.utils.ipmi

class IpmiClient {

	fun sendPing(address : String, timeOutMs : Int = 1000, onPong : () -> Unit) {
		TODO()
	}

	fun openSession(address: String, onSession : (IpmiSession) -> Unit) {
		TODO()
	}
}
