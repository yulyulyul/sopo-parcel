package team.sopo.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ProjectConfig {

    @Value("\${project.version}")
    lateinit var projectVersion: String

    @Value("\${project.api.path}")
    lateinit var apiPath: String

    @Value("\${project.api.secure-path}")
    lateinit var securePath: String

    @Value("\${project.api.admin-user-ant-matchers}")
    lateinit var adminUserAntMatchers: String

}