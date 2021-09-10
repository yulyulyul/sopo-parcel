package team.sopo.common.config.security

import team.sopo.common.consts.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl


@Configuration
class RoleConfiguration {
    @Bean
    fun roleHierarchy(): RoleHierarchy? {
        val roleHierarchy = RoleHierarchyImpl()
        roleHierarchy.setHierarchy("ROLE_${Role.ADMIN} > ROLE_${Role.USER} > ROLE_${Role.PUBLIC}")
        return roleHierarchy
    }
}