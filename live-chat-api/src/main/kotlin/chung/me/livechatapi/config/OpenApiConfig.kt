package chung.me.livechatapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

  @Bean
  fun customOpenApi(): OpenAPI {
    val securitySchemeName = "Bearer Auth"
    return OpenAPI().info(
      Info()
        .title("Live Chat API 문서")
    )
      .addSecurityItem(
        SecurityRequirement()
          .addList(securitySchemeName)
      )
      .components(
        Components()
          .addSecuritySchemes(
            securitySchemeName,
            SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
              .bearerFormat("JWT")
          )
      )
  }
}
