package com.github.K0zka.kerub.exceptions.mappers

import com.github.K0zka.kerub.testBaseUrl
import com.github.K0zka.kerub.testRestBaseUrl
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import javax.ws.rs.core.MediaType

public class JsonMappingExceptionMapperIT {

	Test
	fun brokenJsonFormat() {
		val client = HttpClientBuilder.create().build()
		val post = HttpPost("${testRestBaseUrl}/auth/login")
		post.setHeader("Content-Type", MediaType.APPLICATION_JSON)
		post.setEntity(StringEntity("{username:'',password:''}"))
		val response = client.execute(post)
		Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.`is`(HttpStatus.SC_NOT_ACCEPTABLE))
	}

	Test
	fun brokenJsonMapping() {
		val client = HttpClientBuilder.create().build()
		val post = HttpPost("${testRestBaseUrl}/auth/login")
		post.setHeader("Content-Type", MediaType.APPLICATION_JSON)
		post.setEntity(StringEntity(
				"""{"username__":"foo","password__":"bar"}"""))
		val response = client.execute(post)
		Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.`is`(HttpStatus.SC_NOT_ACCEPTABLE))
	}

}