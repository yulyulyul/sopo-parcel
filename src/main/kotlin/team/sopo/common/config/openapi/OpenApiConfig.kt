package team.sopo.common.config.openapi

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.springframework.context.annotation.Configuration

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
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT",
        `in` = SecuritySchemeIn.HEADER
    )
)

class OpenApiConfig