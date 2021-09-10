package team.sopo.common.config.openapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.*
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile( value = ["stage","local", "prelive"])
@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "\${sopo-swagger.title}",
        description = "\${sopo-swagger.description}",
        version = "\${sopo-swagger.version}",
        contact = Contact(
            name = "\${sopo-swagger.contact.name}",
            email = "\${sopo-swagger.contact.email}",
            url = "\${sopo-swagger.contact.url}"
        ),
        license = License(
            name = "\${sopo-swagger.license}",
            url = "\${sopo-swagger.licenseUrl}"
        )
    )
)
@SecuritySchemes(
    SecurityScheme(
        name = "BearerToken",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
    ),
    SecurityScheme(
        name = "Oauth2",
        type = SecuritySchemeType.OAUTH2,
        flows = OAuthFlows(
            password = OAuthFlow(
                tokenUrl = "\${sopo.authsv.token-url}",
                scopes = [
                    OAuthScope(name = "read", description = "읽기"),
                    OAuthScope(name = "write", description = "쓰기")
                ]
            )
        ))
)

class OpenApiConfig