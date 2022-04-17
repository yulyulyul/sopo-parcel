package team.sopo.common.config.auth

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService {

    fun loadUserByUsername(userId: String, roles: List<*>): UserDetails {
        val authorities = roles
            .filterIsInstance<String>()
            .map { role -> SimpleGrantedAuthority(role) }
            .toList()

        return User(
            userId,
            "",
            authorities
        )
    }

}