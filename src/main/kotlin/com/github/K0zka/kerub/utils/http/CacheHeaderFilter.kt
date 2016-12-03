package com.github.K0zka.kerub.utils.http

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

class CacheHeaderFilter : Filter {
	override fun destroy() {
	}

	override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
		(response as HttpServletResponse).setHeader("Cache-Control", "public, max-age=600000")
		chain.doFilter(request, response)
	}

	override fun init(filterConfig: FilterConfig?) {
	}

}