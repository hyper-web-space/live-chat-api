package chung.me.livechatapi

import chung.me.livechatapi.config.EmbeddedRedisConfiguration
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.net.URI

@TestPropertySource(properties = ["de.flapdoodle.mongodb.embedded.version=3.6.5"])
@AutoConfigureDataMongo
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@Import(EmbeddedRedisConfiguration::class)
abstract class SpringMvcMockTestSupport {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var objectMapper: ObjectMapper

  fun parseJson(value: Any): String {
    return objectMapper.writeValueAsString(value)
  }

  fun performGet(url: String, params: Map<String, String>? = null, headers: Map<String, Any>? = null): ResultActions {
    return getBuilder(MockMvcRequestBuilders::get, url, params, null, headers).let {
      mockMvc.perform(it)
    }
  }

  private fun getBuilder(
    method: (URI) -> MockHttpServletRequestBuilder,
    url: String,
    params: Map<String, String>? = null,
    body: Any? = null,
    headers: Map<String, Any>? = null,
  ): MockHttpServletRequestBuilder {
    val builder = method(URI.create(url))
      .contentType(MediaType.APPLICATION_JSON)

    addParams(params, builder)
    addBody(body, builder)
    addHeaders(headers, builder)

    return builder
  }

  private fun addParams(
    params: Map<String, String>?,
    builder: MockHttpServletRequestBuilder,
  ) {
    params?.forEach { (key, value) ->
      builder.param(key, value)
    }
  }

  private fun addBody(
    body: Any?,
    builder: MockHttpServletRequestBuilder,
  ) {
    body?.let {
      builder.content(parseJson(it))
    }
  }

  private fun addHeaders(
    headers: Map<String, Any>?,
    builder: MockHttpServletRequestBuilder,
  ) {
    headers?.let {
      it.forEach { (k, v) ->
        builder.header(k, v)
      }
    }
  }

  fun performPost(url: String, body: Any? = null, headers: Map<String, Any>? = null): ResultActions {
    return getBuilder(MockMvcRequestBuilders::post, url, null, body, headers).let {
      mockMvc.perform(it)
    }
  }

  fun performPut(url: String, body: Any? = null, headers: Map<String, Any>? = null): ResultActions {
    return getBuilder(MockMvcRequestBuilders::put, url, null, body, headers).let {
      mockMvc.perform(it)
    }
  }

  fun performDelete(
    url: String,
    params: Map<String, String>? = null,
    headers: Map<String, Any>? = null,
  ): ResultActions {
    return getBuilder(MockMvcRequestBuilders::delete, url, params, null, headers).let {
      mockMvc.perform(it)
    }
  }

  final inline fun <reified T> toResult(response: MockHttpServletResponse): T {
    return toResult(response.contentAsString)
  }

  final inline fun <reified T> toResult(json: String): T {
    val typeReference = object : TypeReference<T>() {}
    return objectMapper.readValue(json, typeReference)
  }
}
