package com.github.K0zka.kerub.security

import com.github.K0zka.kerub.utils.NOP
import com.github.K0zka.kerub.utils.silent
import org.apache.shiro.SecurityUtils
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

class WebsocketSecurityInterceptor : HandshakeInterceptor {
	override fun afterHandshake(
			request: ServerHttpRequest,
			response: ServerHttpResponse,
			wsHandler: WebSocketHandler,
			exception: Exception?) = NOP()

	override fun beforeHandshake(
			request: ServerHttpRequest,
			response: ServerHttpResponse,
			wsHandler: WebSocketHandler,
			attributes: MutableMap<String, Any>): Boolean {
		val authenticated = silent { SecurityUtils.getSubject() }?.isAuthenticated ?: false
		if (authenticated) {
			attributes.put("subject", SecurityUtils.getSubject())
		}
		return authenticated
	}
}