package team.sopo.common.config.jwt

data class JwtToken(
    val grantType: String,
    val userToken: String,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpireDate: Long
)