package com.github.kerubistan.kerub.services.exc.mappers

import com.github.kerubistan.kerub.testRestBaseUrl
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import javax.ws.rs.core.MediaType

class JsonMappingExceptionMapperIT {

	@Test
	fun brokenJsonFormat() {
		val client = HttpClientBuilder.create().build()
		val post = HttpPost("$testRestBaseUrl/auth/login")
		post.setHeader("Content-Type", MediaType.APPLICATION_JSON)
		post.entity = StringEntity("{username:'',password:''}")
		val response = client.execute(post)
		Assert.assertThat(response.statusLine.statusCode, CoreMatchers.`is`(HttpStatus.SC_NOT_ACCEPTABLE))
	}

	@Test
	fun brokenJsonMapping() {
		val client = HttpClientBuilder.create().build()
		val post = HttpPost("$testRestBaseUrl/auth/login")
		post.setHeader("Content-Type", MediaType.APPLICATION_JSON)
		post.entity = StringEntity(
				"""{"username__":"foo","password__":"bar"}""")
		val response = client.execute(post)
		Assert.assertThat(response.statusLine.statusCode, CoreMatchers.`is`(HttpStatus.SC_NOT_ACCEPTABLE))
	}

}