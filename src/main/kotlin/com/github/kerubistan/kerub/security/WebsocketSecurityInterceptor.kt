package com.github.kerubistan.kerub.security

import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.silent
import org.apache.shiro.SecurityUtils
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

class WebsocketSecurityInterceptor : HandshakeInterceptor {
	override fun afterHandshake(
			request: ServerHttpRequest,
			response: ServerHttpResponse,
			wsHandler: WebSocketHandler,
			exception: Exception?) {
		// intentionally nothing
	}

	override fun beforeHandshake(
			request: ServerHttpRequest,
			response: ServerHttpResponse,
			wsHandler: WebSocketHandler,
			attributes: MutableMap<String, Any>): Boolean {
		val authenticated = silent(level = LogLevel.Debug, actionName = "get subject") {
			SecurityUtils.getSubject()
		}?.isAuthenticated ?: false
		if (authenticated) {
			attributes["subject"] = SecurityUtils.getSubject()
		}
		return authenticated
	}
}